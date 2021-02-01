package com.example.medicalhlepers.PersonalInformation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.medicalhelpers.R;
import java.text.SimpleDateFormat;
import org.litepal.crud.DataSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PersonalInformation extends AppCompatActivity {
    private RelativeLayout changeImage;
    private RelativeLayout changeName;
    private RelativeLayout changeSex;
    private RelativeLayout changeBirthday;
    private RelativeLayout changeIdType;
    private RelativeLayout changeIdNumber;
    private RelativeLayout changePhoneNumber;
    private TextView name;
    private TextView sex;
    private TextView idType;
    private TextView idNumber;
    private TextView phoneNumber;
    private TextView birthdayText;
    private ImageView getBack;
    private PersonalMessageStore personalMessageStore;
    private List<PersonalMessageStore> personalMessageStores;

    private ImageView iv_photo;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private Dialog upSex;
    private String UserSex;
    private String dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        changeImage = (RelativeLayout) findViewById(R.id.rl_avator);
        changeName = (RelativeLayout) findViewById(R.id.rl_nickname);
        changeSex = (RelativeLayout) findViewById(R.id.rl_sex);
        changeBirthday = (RelativeLayout) findViewById(R.id.birthday);
        changeIdType = (RelativeLayout) findViewById(R.id.rl_auth);
        changeIdNumber = (RelativeLayout) findViewById(R.id.id_number);
        changePhoneNumber = (RelativeLayout) findViewById(R.id.rl_bindphone);
        iv_photo = (ImageView) findViewById(R.id.img_avator);
        sex = (TextView) findViewById(R.id.tv_sex);
        name = (TextView) findViewById(R.id.tv_nickname);
        idType = (TextView) findViewById(R.id.id_type);
        idNumber = (TextView) findViewById(R.id.id_number_1);
        phoneNumber = (TextView) findViewById(R.id.tv_bindphone);
        birthdayText = (TextView) findViewById(R.id.tv_birthday);

        personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStores.get(0);
        sex.setText(personalMessageStore.getUserSex());
        name.setText(personalMessageStore.getUserName());
        idType.setText(personalMessageStore.getIdType());
        idNumber.setText(personalMessageStore.getIdNumber());
        phoneNumber.setText(personalMessageStore.getPhoneNumber());
        birthdayText.setText(personalMessageStore.getUserbirthDay());

        changeImage.setOnClickListener(new View.OnClickListener() { //修改头像
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
        changeSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSex();
            }
        });
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInformation.this, ChangeName.class);
                startActivity(intent);
            }
        });
        changeIdType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
        changeIdNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInformation.this, ChangeIdNumber.class);
                startActivity(intent);
            }
        });

        changeBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
        changePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInformation.this, BindingPhone.class);
                startActivity(intent);
            }
        });
        //返回
        getBack = (ImageView) findViewById(R.id.img_back);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //日期选择器
    public void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        //birthdayText.setText(DateUtils.date2String(calendar.getTime(), DateUtils.YMD_FORMAT));
        DatePickerDialog dialog = new DatePickerDialog(PersonalInformation.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dates = dateformat.format(calendar.getTime());
                        birthdayText.setText(dates);
                        //更新数据
                        PersonalMessageStore personalMessageStore1 = new PersonalMessageStore();
                        personalMessageStore1.setUserbirthDay(dates);
                        personalMessageStore1.updateAll();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    //发送数据
                                    RequestBody requestBody = new FormBody.Builder()
                                            .add("phone", personalMessageStore.getPhoneNumber())
                                            .add("name","")
                                            .add("passwd", "")
                                            .add("sex", "")
                                            .add("idType", "")
                                            .add("idNumber", "")
                                            .add("birthday", dates).build();
                                    Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyUserInfo").post(requestBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        personalMessageStores = DataSupport.findAll(PersonalMessageStore.class);
        personalMessageStore = personalMessageStores.get(0);
        sex.setText(personalMessageStore.getUserSex());
        name.setText(personalMessageStore.getUserName());
        String username = personalMessageStore.getUserName();
        idType.setText(personalMessageStore.getIdType());
        idNumber.setText(personalMessageStore.getIdNumber());
        phoneNumber.setText(personalMessageStore.getPhoneNumber());
        birthdayText.setText(personalMessageStore.getUserbirthDay());
    }
    /*
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        ActivityCompat.requestPermissions(PersonalInformation.this,
                                new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        takePicture();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void selectSex() {
        upSex = new Dialog(PersonalInformation.this, R.style.dialog);
        View defaultView = LayoutInflater.from(this).inflate(R.layout.picker_dialog, null);
        PickerView picker = (PickerView) defaultView.findViewById(R.id.pickerView);
        Button finish = (Button) defaultView.findViewById(R.id.finish);
        Button cancel = (Button) defaultView.findViewById(R.id.cancel);
        List<String> data = new ArrayList<String>();
        data.add("男");
        data.add("女");
        picker.setData(data);
        picker.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) { //在这里获取选择到的性别
                UserSex = text;
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upSex.dismiss();
                PersonalMessageStore personalMessageStore1 = new PersonalMessageStore();
                personalMessageStore1.setUserSex(UserSex);
                personalMessageStore1.updateAll();   //更新数据
                sex.setText(UserSex);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            //发送数据
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("phone", personalMessageStore.getPhoneNumber())
                                    .add("name","")
                                    .add("passwd", "")
                                    .add("sex", UserSex)
                                    .add("idType", "")
                                    .add("idNumber", "")
                                    .add("birthday", "").build();
                            Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyUserInfo").post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upSex.dismiss();
            }
        });
        upSex.setContentView(defaultView);
        upSex.setCancelable(true);
        upSex.setCanceledOnTouchOutside(false);
        upSex.show();
        WindowManager windowManager = PersonalInformation.this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = upSex.getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        upSex.getWindow().setAttributes(lp);
        upSex.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void showCustomDialog(){
        final String userIdType;
        final Dialog mDialog = new Dialog(this, R.style.CustomDialogTheme);
        //获取要填充的布局
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.send_tel_dailog,null);
        //设置自定义的dialog布局
        mDialog.setContentView(v);
        //false表示点击对话框以外的区域对话框不消失，true则相反
        mDialog.setCanceledOnTouchOutside(false);
        final WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = 800;
        mDialog.getWindow().setAttributes(params);
        mDialog.show();
        //获取自定义dialog布局控件
        Button shenfenzhengBt = (Button)v.findViewById(R.id.shenfenzheng);
        Button hukoubuBt = (Button) v.findViewById(R.id.hukoubu);
        Button zhongguoBt = (Button) v.findViewById(R.id.zhongguohuzhao);
        Button waiguoBt = (Button) v.findViewById(R.id.waiguohuzhao);
        Button gangaoBt = (Button) v.findViewById(R.id.gangaotongxing);
        Button taiwanBt = (Button) v.findViewById(R.id.taiwan);
        Button shibingBt = (Button) v.findViewById(R.id.shibingzheng);
        //身份证
        shenfenzhengBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                personalMessageStore.setIdType("身份证");
                personalMessageStore.updateAll();
                idType.setText("身份证");
                mDialog.dismiss();
            }
        });
        //户口簿
        hukoubuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                personalMessageStore.setIdType("户口簿");
                personalMessageStore.updateAll();
                idType.setText("户口簿");
                updateIdType(personalMessageStore.getIdType());
                mDialog.dismiss();
            }
        });
        //中国护照
        zhongguoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalMessageStore.setIdType("中国护照");
                personalMessageStore.updateAll();
                idType.setText("中国护照");
                updateIdType(personalMessageStore.getIdType());
                mDialog.dismiss();
            }
        });
        //外国护照
        waiguoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalMessageStore.setIdType("外国护照");
                personalMessageStore.updateAll();
                idType.setText("外国护照");
                updateIdType(personalMessageStore.getIdType());
                mDialog.dismiss();
            }
        });
        //港澳通行证
        gangaoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalMessageStore.setIdType("港澳通行证");
                personalMessageStore.updateAll();
                idType.setText("港澳通行证");
                updateIdType(personalMessageStore.getIdType());
                mDialog.dismiss();
            }
        });
        //台湾通行证
        taiwanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalMessageStore.setIdType("台湾通行证");
                personalMessageStore.updateAll();
                idType.setText("台湾通行证");
                updateIdType(personalMessageStore.getIdType());
                mDialog.dismiss();
            }
        });
        //士兵证
        shibingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalMessageStore.setIdType("解放军士兵证");
                personalMessageStore.updateAll();
                updateIdType(personalMessageStore.getIdType());
                idType.setText("解放军士兵证");
                mDialog.dismiss();
            }
        });
    }

    private void updateIdType(final String userIdType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //发送数据
                    RequestBody requestBody = new FormBody.Builder()
                            .add("phone", personalMessageStore.getPhoneNumber())
                            .add("name","")
                            .add("passwd", "")
                            .add("sex", "")
                            .add("idType", userIdType)
                            .add("idNumber", "")
                            .add("birthday", "").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyUserInfo").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param
     * @param
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            iv_photo.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
        }
    }

    private void takePicture() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            // 需要申请动态权限
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        Intent openCameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment
                .getExternalStorageDirectory(), "image.jpg");
        try {
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= 24) {
            openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            tempUri = FileProvider.getUriForFile(PersonalInformation.this, "com.example." +
                    "medicalhlepers.fileProvider", file);
        } else {
            tempUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "image.jpg"));
        }
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }
}

