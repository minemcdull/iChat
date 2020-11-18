package must.example.com.myapplication;

/**
 * Created by Lenovo on 2017/4/10.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ImageHandle extends Activity {

    private Button showImg = null;
    private ImageView img = null;
    private static final String IMG_TYPE = "image/*";
    private static final int IMG_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        showImg = (Button) findViewById(R.id.ShowImg);
        img = (ImageView) findViewById(R.id.Img);
        showImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(IMG_TYPE);
                startActivityForResult(intent, IMG_CODE);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // RESULT_OK为系统定义的常量
        if(resultCode != RESULT_OK) {
            return;
        }
        ContentResolver resolver = getContentResolver();

        if (resultCode == RESULT_OK) {
            // 获取图片uri
            Uri uri = data.getData();
            try {
                // 显示到bitmap图片
                MediaStore.Images.Media.getBitmap(resolver, uri);
                // 获取图片的路径
                String[] proj = new String[]{MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);

                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                // 根据索引值获取图片路径
                String path = cursor.getString(index);
                // ImageView里面打印图片
                img.setImageURI(uri);

                // 图片上传服务器
                File file =new File(path);
                Upload upload = new Upload();
                upload.uploadFile(file);

                // 输出本文件存在的位置
                System.out.println(path);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}