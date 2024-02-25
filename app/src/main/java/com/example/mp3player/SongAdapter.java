package com.example.mp3player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp3player.Model.Song;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    private List<Song> songs;
    private Activity context;
    private MediaPlayer mediaPlayer;
    private Boolean isPlaying = false;

    public SongAdapter(List<Song> songs, Activity context) {
        this.songs = songs;
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_song, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.songName.setText(song.name);

        if (song.isDownload) {
            holder.download.setVisibility(View.GONE);
            holder.play.setOnClickListener(v -> toggleMediaPlayer(song));
        } else {
            holder.play.setEnabled(false);
            holder.download.setOnClickListener(v -> downloadSong(holder, song));
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private void toggleMediaPlayer(Song song) {
        if (isPlaying) {
            mediaPlayer.pause();
            mediaPlayer.reset();
            isPlaying = false;
        } else {
            try {
                mediaPlayer.setDataSource(song.storedLink);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadSong(MyViewHolder holder, Song song) {
        holder.downloadProgress.setVisibility(View.VISIBLE);
        new DownloadFile(url -> {
            song.isDownload = true;
            song.storedLink = url.toString();
            notifyDataSetChanged();
            holder.play.setEnabled(true);
            holder.download.setVisibility(View.GONE);
            holder.downloadProgress.setVisibility(View.GONE);
        }, progress -> holder.downloadProgress.setText("Downloading " + progress + "%..."))
                .execute(song.downloadLink, song.name + ".mp3");
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView songName, downloadProgress;
        MaterialButton download, play;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.single_song_name_text);
            download = itemView.findViewById(R.id.single_song_download_btn);
            play = itemView.findViewById(R.id.single_song_play_btn);
            downloadProgress = itemView.findViewById(R.id.single_song_download_progress_txt);
        }
    }
}
