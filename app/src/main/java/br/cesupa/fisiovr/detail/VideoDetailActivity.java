package br.cesupa.fisiovr.detail;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.google.gson.Gson;

import java.io.File;

import br.cesupa.fisiovr.R;
import br.cesupa.fisiovr.dummy.VideoContent;
import br.cesupa.fisiovr.home;
import br.cesupa.fisiovr.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link home}.
 */
public class VideoDetailActivity extends AppCompatActivity {

    public static final String ARG_VIDEO_ID = "video_id";
    FloatingActionButton fab;
    DownloadManager downloadManager;
    long idVideoYoutubeURI;
    String pathDownload;
    private VideoContent.VideoItem mItem;
    BroadcastReceiver ACTION_DOWNLOAD_COMPLETE_BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
            if (id == idVideoYoutubeURI) {
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                        String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(downloadMimeType);
                        if (Util.renameFile(new File(Environment.getExternalStoragePublicDirectory(pathDownload), mItem.id), mItem.id.concat("." + ext)).exists()) {
                            Toast.makeText(VideoDetailActivity.this, "Download finalizado com Sucesso", Toast.LENGTH_LONG).show();
                    } else {
                            Toast.makeText(VideoDetailActivity.this, "Falha ao configurar o Arquivo", Toast.LENGTH_LONG).show();
                    }
                } else {
                        Toast.makeText(VideoDetailActivity.this, "Falha ao fazer o Download do Arquivo", Toast.LENGTH_LONG).show();
                }
                } else {
                    Toast.makeText(VideoDetailActivity.this, "Falha ao capturar informações do Video", Toast.LENGTH_LONG).show();
            }
                cursor.close();
        }
        }
    };
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar_video_detail_activity);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_video_detail_activity);

        progressDialog = new ProgressDialog(VideoDetailActivity.this);
        progressDialog.setMessage("Carregando");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        pathDownload = Environment.DIRECTORY_MOVIES.concat("/" + getString(R.string.app_name));
        if (getIntent().getExtras().containsKey(ARG_VIDEO_ID)) {
            Gson gson = new Gson();
            mItem = gson.fromJson(getIntent().getExtras().getString(ARG_VIDEO_ID), VideoContent.VideoItem.class);

            if (mItem != null) {
                actionBar.setTitle(mItem.snippet.title);
                ((TextView) findViewById(R.id.text_view_video_detail_activity)).setText(mItem.snippet.description);

                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        progressDialog.show();

                        YouTubeExtractor.create()
                                .extract(mItem.id)
                                .enqueue(new Callback<YouTubeExtractionResult>() {
                                             @Override
                                             public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
                                                 try {
                                                     Uri videoYoutubeUri = response.body().getBestAvailableQualityVideoUri();
                                                     if (videoYoutubeUri != null) {
                                                         DownloadManager.Request request = new DownloadManager.Request(response.body().getBestAvailableQualityVideoUri());
                                                         request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                                         request.setDestinationInExternalPublicDir(pathDownload, mItem.id);
                                                         request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                                                         request.allowScanningByMediaScanner();
                                                         request.setTitle(mItem.snippet.title);

                                                         registerReceiver(ACTION_DOWNLOAD_COMPLETE_BroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                                         idVideoYoutubeURI = ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);

                                                         Toast.makeText(VideoDetailActivity.this, "Por motivo de segurança, só será permitido fazer o Download do vídeo por uma rede WIFI", Toast.LENGTH_LONG).show();
                                                     } else {
                                                         Toast.makeText(VideoDetailActivity.this, "Falha ao capturar informações para o Download do Video", Toast.LENGTH_LONG).show();
                                                     }
                                                 } catch (Exception e) {
                                                     e.printStackTrace();
                                                     progressDialog.dismiss();
                                                     Toast.makeText(VideoDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                 } finally {
                                                     progressDialog.dismiss();
                                                 }
                                             }

                                             @Override
                                             public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
                                                 t.printStackTrace();
                                                 progressDialog.dismiss();
                                                 Toast.makeText(VideoDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                             }
                                         }
                                );
                    }
                });
            }
        }
    }
}
