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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
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
    private Bitmap head;// 头像Bitmap
    private static String path = "/sdcard/myHead/";// sd路径

    private CircleImageView iv_photo;
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
        initView();
        initListener();
        changeImage = (RelativeLayout) findViewById(R.id.rl_avator);
        changeName = (RelativeLayout) findViewById(R.id.rl_nickname);
        changeSex = (RelativeLayout) findViewById(R.id.rl_sex);
        changeBirthday = (RelativeLayout) findViewById(R.id.birthday);
        changeIdType = (RelativeLayout) findViewById(R.id.rl_auth);
        changeIdNumber = (RelativeLayout) findViewById(R.id.id_number);
        changePhoneNumber = (RelativeLayout) findViewById(R.id.rl_bindphone);
        iv_photo = (CircleImageView) findViewById(R.id.img_avator);
        sex = (TextView) findViewById(R.id.tv_sex);
        name = (TextView) findViewById(R.id.tv_nickname);
        idType = (TextView) findViewById(R.id.id_type);
        idNumber = (TextView) findViewById(R.id.id_number_1);
        phoneNumber = (TextView) findViewById(R.id.tv_bindphone);
        birthdayText = (TextView) findViewById(R.id.tv_birthday);
        getBack = (ImageView) findViewById(R.id.image_back);

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
                showTypeDialog();
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
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initView() {
        iv_photo = (CircleImageView) findViewById(R.id.img_avator);
        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");// 从SD卡中找头像，转换成Bitmap
        if (bt != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);// 转换成drawable
            iv_photo.setImageDrawable(drawable);
        } else {
/**
 * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
 *
 */
        }
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
                        personalMessageStore.setUserbirthDay(dates);
                        personalMessageStore.updateAll();
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
                                            .add("birthday", dates)
                                            .add("province", "")
                                            .add("city", "")
                                            .add("district", "").build();
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
                personalMessageStore.setUserSex(UserSex);
                personalMessageStore.save();   //更新数据
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
                                    .add("birthday", "")
                                    .add("province", "")
                                    .add("city", "")
                                    .add("district", "").build();
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
                personalMessageStore.setIdType("身份证");
                personalMessageStore.updateAll();
                idType.setText("身份证");
                updateIdType(personalMessageStore.getIdType());
                mDialog.dismiss();
            }
        });
        //户口簿
        hukoubuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            .add("birthday", "")
                            .add("province", "")
                            .add("city", "")
                            .add("district", "").build();
                    Request request = new Request.Builder().url("http://39.96.41.6:8080/modifyUserInfo").post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void initListener() {
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });
    }

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener(){// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                    /**
                     * 上传服务器代码 head是bitmap类型
                     */
                        setPicToView(head);// 保存在SD卡中
                        iv_photo.setImageBitmap(head);// 用ImageView显示出来
                        try {

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("upload_name", "陈悦");
                            jsonObject.put("upload_type", "头像");
                            jsonObject.put("longitude", 132.2);
                            jsonObject.put("latitude", 68.58);
                            jsonObject.put("upload_address", "四川成都");
                            jsonObject.put("upload_time", new Date().getTime());
                            jsonObject.put("upload_description", "这是我的头像");
                            jsonObject.put("approval_status", 2);
                            Map<String, String> params = new HashMap<>();
                            //设置编码类型为utf-8
                            params.put("data", String.valueOf(jsonObject));


                            String BOUNDARY = "--------------et567z"; //数据分隔线
                            String MULTIPART_FORM_DATA = "Multipart/form-data";
                            URL url = new URL("http://39.96.41.6:8080/uploadImage");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true);//允许输入
                            conn.setDoOutput(true);//允许输出
                            conn.setUseCaches(false);//不使用Cache
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("Charset", "UTF-8");
                            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);

                            StringBuilder sb = new StringBuilder();
                            System.out.println("上传名称:"+params.get("data"));
                            //上传的表单参数部分，不需要更改
                            for (Map.Entry<String, String> entry : params.entrySet()) {//构建表单字段内容
                                sb.append("--");
                                sb.append(BOUNDARY);
                                sb.append("\r\n");
                                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                                sb.append(entry.getValue());
                                sb.append("\r\n");
                            }

                            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
                            outStream.write(sb.toString().getBytes());//发送表单字段数据
                            //bitmap转byte[]
                            int bytes = head.getByteCount();
                            ByteBuffer buffer = ByteBuffer.allocate(bytes);
                            head.copyPixelsToBuffer(buffer);
                            //Move the byte data to the buffer
                            byte[] content = buffer.array();
                            //再次设置报头信息
                            StringBuilder split = new StringBuilder();
                            split.append("--");
                            split.append(BOUNDARY);
                            split.append("\r\n");

                            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!非常重要
                            //此处将图片的name设置为file ,filename不做限制，不需要管
                            split.append("Content-Disposition: form-data;name=\"file\";filename=\"temp.jpg\"\r\n");
                            //这里的Content-Type非常重要，一定要是图片的格式，例如image/jpeg或者image/jpg
                            //服务器端对根据图片结尾进行判断图片格式到底是什么,因此务必保证这里类型正确
                            split.append("Content-Type: image/png\r\n\r\n");
                            outStream.write(split.toString().getBytes());
                            outStream.write(content, 0, content.length);
                            outStream.write("\r\n".getBytes());
                            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();//数据结束标志
                            outStream.write(end_data);
                            outStream.flush();

                            //返回状态判断
                            int cah = conn.getResponseCode();
                            //            if (cah != 200) throw new RuntimeException("请求url失败:"+cah);
                            if (cah == 200)//如果发布成功则提示成功
                            {
                                System.out.println("上传成功");
                            } else if (cah == 400) {
                                System.out.println("400错误");
                            } else {
                                throw new RuntimeException("请求url失败:" + cah);
                            }

                            outStream.close();
                            conn.disconnect();


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
// aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
// outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "head.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
// 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


