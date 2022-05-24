package com.softwareproject.travelguideapp.pages.auth.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Base64
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.Button
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.softwareproject.travelguideapp.R
import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import com.softwareproject.travelguideapp.pages.landmark.LandmarkFragment
import com.softwareproject.travelguideapp.util.ActivityUtil
import com.softwareproject.travelguideapp.util.HttpRequest
import com.softwareproject.travelguideapp.util.JsonParser
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class CameraFragment : Fragment(), RequestCallBackListener {

    private lateinit var textureView: TextureView
    private val orientationsArr = SparseIntArray()

    private var cameraId: String? = null
    protected var cameraDevice: CameraDevice? = null
    protected var cameraCaptureSessions: CameraCaptureSession? = null
    protected var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null
    private var imageReader: ImageReader? = null
    private val REQUEST_CAMERA_PERMISSION = 200
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private var handler: Handler? = null
    private val TAKE_PICTURE_MSEC = 7000L

    private var locationManager: LocationManager? = null
    private var LOCATION_REFRESH_TIME = 1000 * 60 * 1L
    private var LOCATION_REFRESH_DIST = 10.0f
    private var LOCATION_REQUEST_CODE = 123
    private var currentLat: Double? = null
    private var currentLong: Double? = null

    private lateinit var imageView: Button


    var requestListener: RequestCallBackListener? = null

    companion object{
        const val TAG = "CameraFragment"
    }

    override fun onRequestCompleted(result : JSONObject?, error : JSONObject?) {
        if(result != null){
            val landmarkInfo = JsonParser().parseLandmarkDetails(result)
            val bundle = Bundle()
            bundle.putInt("landmarkId", landmarkInfo.score.landmarkId)
            bundle.let {
                val landmarkFragment = LandmarkFragment()
                landmarkFragment.arguments = it
                val mSupportManager = activity?.supportFragmentManager
                mSupportManager?.let {
                    ActivityUtil().changeFragmentWithStack(landmarkFragment,
                        LandmarkFragment.TAG,
                        mSupportManager,
                        R.id.act_home_fl_container)
                }
            }
        }
        else if (error != null){
            val mContext = context
            mContext?.let {
                val title = mContext.getString(R.string.header_error)
                val message = JsonParser().parseError(error)
                ActivityUtil().openAlertDialog(mContext,title, message, false){}
            }
        }
    }

    private val textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

    }

    private val stateCallBack: CameraDevice.StateCallback = object : CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUserPermission()
        val rootView = inflater.inflate(R.layout.fragment_camera_preview , container, false)
        orientationsArr.put(Surface.ROTATION_0, 90)
        orientationsArr.put(Surface.ROTATION_90, 0)
        orientationsArr.put(Surface.ROTATION_180, 270)
        orientationsArr.put(Surface.ROTATION_270, 180)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setViews()
        requestListener = this
    }

    private fun init(view : View){
        textureView = view.findViewById(R.id.act_camera_textureview_texture)
        imageView = view.findViewById(R.id.button)
    }

    private fun setViews(){
        textureView.surfaceTextureListener = textureListener

        imageView.setOnClickListener {takePicture()

        }
    }

    private fun getUserPermission(){
        val mContext = context
        mContext?.let {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_REQUEST_CODE)

            }
            else{
                attachLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun attachLocation() {
        locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
    }

    private var locationListener  = object: LocationListener{
        override fun onLocationChanged(location: Location?) {
            currentLat =  location?.latitude
            currentLong = location?.longitude
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && permissions[1] == Manifest.permission.ACCESS_FINE_LOCATION
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                attachLocation()
            }
        }
    }

    protected fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    protected fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e : InterruptedException){
            e.printStackTrace()
        }
    }
    protected fun takePicture(){
        if (cameraDevice == null) return
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
        val jpegSizes  = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(
            ImageFormat.JPEG)
        var width = 640;
        var height = 480;
        if (jpegSizes != null && jpegSizes.isNotEmpty()) {
            width = jpegSizes[0].width;
            height = jpegSizes[0].height;
        }
        val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        val outputSurfaces = ArrayList<Surface>(2)
        outputSurfaces.add(reader.surface);
        outputSurfaces.add(Surface(textureView.surfaceTexture))
        val captureBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureBuilder?.addTarget(reader.surface)
        captureBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        // Orientation
        val rotation = activity?.windowManager?.defaultDisplay?.rotation ?: return
        captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, orientationsArr.get(rotation))
        val file = File( "${Environment.getExternalStorageDirectory()}/pic.jpg")
        val readerListener = object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                var image: Image? = null
                try{
                    image = reader?.acquireLatestImage()
                    image?.let {
                        image.planes[0]?.let {
                            val buffer = image.planes[0].buffer
                            buffer?.let {
                                val bytes = ByteArray(buffer.capacity())
                                buffer.get(bytes)
                                // Image is ready send it to the server to recognize
                                sendImage(bytes, requestListener)
                                saveImage(bytes)
                            }
                        }
                    }
                }
                catch (e: FileNotFoundException){
                    e.printStackTrace()
                }
                catch (e: IOException){
                    e.printStackTrace()
                }
                finally{
                    image?.let{
                        image.close()
                    }
                }
            }
            private fun saveImage(bytes: ByteArray){
                var output: OutputStream? = null
                try{
                    output = FileOutputStream(file)
                    output.write(bytes)
                }finally {
                    output?.let { output.close() }
                }
            }

            private fun sendImage(bytes: ByteArray, requestListener: RequestCallBackListener?){
                val requestBody = JSONObject()
                requestBody.put("image", Base64.encodeToString(bytes, Base64.NO_WRAP))
                requestListener?.let {
                    HttpRequest("landmarks/recognize",method = "POST", body = requestBody,listenerRequest = requestListener).execute()
                }
            }
        }
        reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)
        val captureListener = object: CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                createCameraPreview()
            }
        }
        cameraDevice?.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                try{
                    captureBuilder?.let {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler)
                    }
                } catch (e: CameraAccessException){
                    e.printStackTrace()
                }
            }
            override fun onConfigureFailed(session: CameraCaptureSession) {}


        }, mBackgroundHandler)
    }


    private fun createCameraPreview(){
        try{
            val texture = textureView.surfaceTexture
            imageDimension?.let {
                texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
                val surface = Surface(texture)
                captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequestBuilder?.addTarget(surface)
                cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        //The camera is already closed
                        if (null == cameraDevice) {
                            return
                        }
                        // When the session is ready, we start displaying the preview.
                        cameraCaptureSessions = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("Configure Failed", "Config Changed")}

                },null)
            }


        } catch (e: CameraAccessException){
            e.printStackTrace()
        }
    }

    private fun openCamera(){
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try{
            cameraId = manager.cameraIdList[0]
            cameraId?.let {
                val characteristics = manager.getCameraCharacteristics(cameraId!!)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                map?.let {
                    imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
                    val currentAct = activity ?: return
                    if (ActivityCompat.checkSelfPermission(currentAct, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(currentAct, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(currentAct, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
                        return
                    }
                }
            }
            cameraId?.let {
                manager.openCamera(cameraId!!, stateCallBack, null)
            }
        } catch (e: CameraAccessException){
            e.printStackTrace()
        }
    }

    private fun updatePreview(){
        if (cameraDevice == null){
            Log.e("", "updatePreview error")
        }
        captureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            captureRequestBuilder?.let {
                cameraCaptureSessions?.setRepeatingRequest(captureRequestBuilder!!.build(), null, mBackgroundHandler)
            }
        }catch(e : CameraAccessException){
            e.printStackTrace()
        }
    }

    private fun closeCamera(){
        cameraDevice?.close()
        cameraDevice = null
        imageReader?.close()
        imageReader = null
    }


    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable){
            openCamera()
        }
        else{
            textureView.surfaceTextureListener = textureListener

        }
        /*
        handler = Handler()
        handler?.postDelayed(object : Runnable {
            override fun run() {
                //takePicture()
                handler?.postDelayed(this, TAKE_PICTURE_MSEC)
            }
        },TAKE_PICTURE_MSEC )*/
    }

    override fun onPause() {
        stopBackgroundThread()
        closeCamera()
        super.onPause()
    }


}