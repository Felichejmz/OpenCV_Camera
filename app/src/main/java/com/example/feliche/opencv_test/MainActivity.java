package com.example.feliche.opencv_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String TAG = "CameraView:Activity";
    private CameraBridgeViewBase mOpenCvCameraView;

    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;

    public MainActivity(){
        Log.i(TAG,"Nueva instancia" + this.getClass());
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG,"OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    break;
                }
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"Se entro a OnCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.show_camera);
        mOpenCvCameraView = (JavaCameraView)findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this,mLoaderCallback);
        }else{
            Log.d(TAG,"OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width, CvType.CV_8UC4);
        mRgbaF = new Mat(height,width,CvType.CV_8UC4);
        mRgbaT = new Mat(width,width,CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame)
    {
        mRgba = inputFrame.rgba();
        Core.transpose(mRgba,mRgbaT);
        Imgproc.resize(mRgbaT,mRgbaF,mRgbaF.size(),0,0,0);
        Core.flip(mRgbaF,mRgba,1);
        return mRgba;
    }
}
