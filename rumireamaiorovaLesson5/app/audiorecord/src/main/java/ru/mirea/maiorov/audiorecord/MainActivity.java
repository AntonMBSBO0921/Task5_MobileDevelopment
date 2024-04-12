package ru.mirea.maiorov.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import ru.mirea.maiorov.audiorecord.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 200;
    private ActivityMainBinding binding;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private String recordFilePath;
    private MediaRecorder recorder;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Проверка разрешений на запись аудио и запись на внешнюю память
        checkPermissions();

        // Инициализация кнопок записи и воспроизведения
        Button recordButton = binding.recordButton;
        Button playButton = binding.playButton;

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                    isRecording = true;
                    isPlaying = false;
                    playButton.setEnabled(false);
                    recordButton.setText("Stop Recording");
                } else {
                    stopRecording();
                    isRecording = false;
                    recordButton.setText("Начать запись №16 БСБО-09-21");
                    // После остановки записи включаем кнопку воспроизведения
                    playButton.setEnabled(true);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    startPlaying();
                    isPlaying = true;
                    isRecording = false;
                    recordButton.setEnabled(false);
                    playButton.setText("Stop");
                } else {
                    stopPlaying();
                    isPlaying = false;
                    isRecording = true; // После остановки воспроизведения разрешаем запись
                    recordButton.setEnabled(true); // После остановки воспроизведения разрешаем запись
                    playButton.setText("Play");
                }
            }
        });

        // Определение пути файла для записи аудио
        recordFilePath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "/audiorecordtest.3gp").getAbsolutePath();
    }

    // Проверка разрешений на запись аудио и запись на внешнюю память
    private void checkPermissions() {
        int audioRecordPermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (audioRecordPermissionStatus != PackageManager.PERMISSION_GRANTED || storagePermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    // Обработка результатов запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permissions granted");
            } else {
                Log.d("MainActivity", "Permissions denied");
            }
        }
    }

    // Начать запись аудио
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e("MainActivity", "prepare() failed");
        }
    }

    // Остановить запись аудио
    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }



    // Начать воспроизведение аудио
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("MainActivity", "prepare() failed");
        }
    }

    // Остановить воспроизведение аудио
    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}