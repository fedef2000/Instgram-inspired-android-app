package com.example.po_app_project.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.po_app_project.CommentActivity;
import com.example.po_app_project.DBHelper;
import com.example.po_app_project.EditPostActivity;
import com.example.po_app_project.HomeActivity;
import com.example.po_app_project.Models.Post;
import com.example.po_app_project.Models.User;
import com.example.po_app_project.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsHolder> {
    private Context context;
    private ArrayList<Post> list;
    private ArrayList<Post> listAll;
    private SharedPreferences preferences;
    DBHelper databaseHelper;

    public PostsAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,parent,false);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = list.get(position);
        holder.imgProfile.setImageBitmap(post.getUser().getPhoto());
        holder.imgPost.setImageBitmap(post.getImage());
        holder.txtName.setText(post.getUser().getUsername());
        holder.txtLikes.setText(Integer.toString(post.getLikes())+ " Likes");
        holder.txtDate.setText(DateToIta(post.getDate()));
        holder.txtDesc.setText(post.getDescription());
        holder.btnLike.setImageResource(post.isSelfLike()?R.drawable.ic_favorite_red:R.drawable.ic_favorite_outline);
        databaseHelper = new DBHelper(context.getApplicationContext());

        holder.btnLike.setOnClickListener(v->{
            String username = preferences.getString("username","");
            User user = databaseHelper.getUserByUsername(username);
            if (post.isSelfLike()){
                post.setSelfLike(false);
                post.setLikes(post.getLikes()-1);
                databaseHelper.deleteLike(user.getId(),post.getId());
                holder.btnLike.setImageResource(R.drawable.ic_favorite_outline);
                holder.txtLikes.setText(Integer.toString(post.getLikes())+ " Likes");
            }
            else {
                post.setSelfLike(true);
                post.setLikes(post.getLikes()+1);
                databaseHelper.insertLike(user.getId(),post.getId());
                holder.btnLike.setImageResource(R.drawable.ic_favorite_red);
                holder.txtLikes.setText(Integer.toString(post.getLikes())+ " Likes");
            }

        });

        if(post.getUser().getUsername().equals(preferences.getString("username", ""))){
            holder.btnPostOption.setVisibility(View.VISIBLE);
        } else {
            holder.btnPostOption.setVisibility(View.GONE);
        }

        holder.btnPostOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnPostOption);
            popupMenu.inflate(R.menu.menu_post_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.item_edit){
                        Intent i = new Intent(((HomeActivity)context), EditPostActivity.class);
                        i.putExtra("postId",post.getId());
                        i.putExtra("position",position);
                        i.putExtra("text",post.getDescription());
                        context.startActivity(i);
                        return true;

                    }else if(item.getItemId() == R.id.item_delete){
                        deletePost(post.getId(),position);
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        });

        holder.txtComments.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });

        holder.btnComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context),CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PostsHolder extends RecyclerView.ViewHolder{

        private TextView txtName,txtDate,txtDesc,txtLikes,txtComments;
        private CircleImageView imgProfile;
        private ImageView imgPost;
        private ImageButton btnPostOption,btnLike,btnComment;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtPostName);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            txtDesc = itemView.findViewById(R.id.txtPostDesc);
            txtComments = itemView.findViewById(R.id.txtPostComments);
            txtLikes = itemView.findViewById(R.id.txtPostLikes);
            imgProfile = itemView.findViewById(R.id.imgPostProfile);
            imgPost = itemView.findViewById(R.id.imgPostPhoto);
            btnComment = itemView.findViewById(R.id.btnPostComment);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnLike = itemView.findViewById(R.id.btnPostLike);
            btnPostOption.setVisibility(View.GONE);
        }
    }

    private void deletePost(int postId,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Cancellare il post?");
        builder.setPositiveButton("Cancella", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.deletePost(postId);
                list.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                listAll.clear();
                listAll.addAll(list);
                };
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }

    private String DateToIta(String date){
        String day = date.substring(0,2);
        String monthN = date.substring(3,5);
        String year = date.substring(6,10);
        String month;
        if(monthN.equals("1")){
            month = "Gennaio";
        }else if (monthN.equals("2")){
            month = "Febbraio";
        }else if (monthN.equals("3")){
            month = "Marzo";
        }else if (monthN.equals("4")){
            month = "Aprile";
        }else if (monthN.equals("5")){
            month = "Maggio";
        }else if (monthN.equals("6")){
            month = "Giugno";
        }else if (monthN.equals("7")){
            month = "Luglio";
        }else if (monthN.equals("8")){
            month = "Agosto";
        }else if (monthN.equals("9")){
            month = "Settembre";
        }else if (monthN.equals("10")){
            month = "Ottobre";
        }else if (monthN.equals("11")){
            month = "Novembre";
        }else{
            month = "Dicembre";
        }
        return day + " " + month + " " + year;
    }

}