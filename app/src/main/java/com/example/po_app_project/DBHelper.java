package com.example.po_app_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import com.example.po_app_project.Models.Comment;
import com.example.po_app_project.Models.Post;
import com.example.po_app_project.Models.User;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context)
    {
        super(context, "AppDatabase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("create Table users(id INTEGER primary key autoincrement not null, email TEXT unique,  username TEXT unique, password TEXT, profileImg BLOB)");
        MyDatabase.execSQL("create Table posts(id INTEGER primary key autoincrement not null, img BLOB, description TEXT, date TEXT, like_count INT, username_id INT, foreign key (username_id) references users(id))");
        MyDatabase.execSQL("create Table likes(id INTEGER primary key autoincrement not null, user_id INTEGER, Post_id INTEGER, foreign key (user_id) references users(id), foreign key (post_id) references posts(id), unique(user_id,post_id))");
        MyDatabase.execSQL("create Table comments(id INTEGER primary key autoincrement not null,text TEXT, user_id INTEGER, Post_id INTEGER, foreign key (user_id) references users(id), foreign key (post_id) references posts(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
        MyDB.execSQL("drop Table if exists posts");
        MyDB.execSQL("drop Table if exists likes");
        MyDB.execSQL("drop Table if exists comments");
    }


    //USER
    public Boolean insertUser(String email, String username, String password, byte[] image){

        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("username", username);
        contentValues.put("profileImg", image);
        long result = MyDatabase.insert("users", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Boolean checkIfUserExists(String email, String username){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ? OR username = ?", new String[]{email,username});
        if(cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean checkCredentials(String emailOrUsername, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from users where username = ? and password = ?", new String[]{emailOrUsername, password});

        if (c1.getCount() > 0) {
            return true;
        } else{
                return false;
        }
    }
    public User getUserByUsername (String username){
        User result = new User();
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from users where username = ? OR email = ?", new String[]{username, username});
        if (c1.moveToFirst()) {
            result.setUsername(username);
            int i = c1.getColumnIndex("id");
            if(i >= 0){
                result.setId(c1.getInt(i));
            }
            i = c1.getColumnIndex("profileImg");
            if(i > 0){
                byte[] b = c1.getBlob(i);
                Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);
                result.setPhoto(img);
            }
        }
        return result;
    }
    public User getUserById (int id){
        User result = new User();
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from users where id = ?", new String[]{Integer.toString(id)});
        if (c1.moveToFirst()) {
            result.setId(id);
            int i = c1.getColumnIndex("username");
            if(i > 0){
                result.setUsername(c1.getString(i));
            }
            i = c1.getColumnIndex("profileImg");
            if(i > 0){
                byte[] b = c1.getBlob(i);
                Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);
                result.setPhoto(img);
            }
        }
        return result;
    }
    public Boolean updateUser (int id, String newUsername, byte[] newProfileImg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("profileImg", newProfileImg );

        int i = db.update("users", values, "id=?", new String[]{Integer.toString(id)});
        if (i != 1){
            return false;
        }

        return true;
    }


    //POST
    public Boolean insertPost(byte[] img,String description,String date,int like_count,int username_id){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("img", img);
        contentValues.put("description", description);
        contentValues.put("date", date);
        contentValues.put("like_count", like_count);
        contentValues.put("username_id", username_id);

        long result = MyDatabase.insert("posts", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public ArrayList<Post> getLastPosts (int viewerId){
        ArrayList<Post> result = new ArrayList<Post>();
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from posts order by id desc", null);
        if (c1.getCount() > 0) {
            c1.moveToFirst();
            Post post = new Post();
            int i = c1.getColumnIndex("id");
            post.setId(c1.getInt(i));
            i = c1.getColumnIndex("img");
            byte[] b = c1.getBlob(i);
            Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);
            post.setImage(img);
            i = c1.getColumnIndex("description");
            post.setDescription(c1.getString(i));
            i = c1.getColumnIndex("date");
            post.setDate(c1.getString(i));
            i = c1.getColumnIndex("like_count");
            post.setLikes(c1.getInt(i));
            i = c1.getColumnIndex("username_id");
            post.setUser(getUserById(c1.getInt(i)));
            post.setSelfLike(isLikedByUserId(viewerId, post.getId()));
            post.setLikes(getLikesByPostId(post.getId()));
            result.add(post);

            while(c1.moveToNext()){
                post = new Post();
                i = c1.getColumnIndex("id");
                post.setId(c1.getInt(i));
                i = c1.getColumnIndex("img");
                b = c1.getBlob(i);
                img = BitmapFactory.decodeByteArray(b, 0, b.length);
                post.setImage(img);
                i = c1.getColumnIndex("description");
                post.setDescription(c1.getString(i));
                i = c1.getColumnIndex("date");
                post.setDate(c1.getString(i));
                i = c1.getColumnIndex("like_count");
                post.setLikes(c1.getInt(i));
                i = c1.getColumnIndex("username_id");
                post.setUser(getUserById(c1.getInt(i)));
                post.setSelfLike(isLikedByUserId(viewerId, post.getId()));
                post.setLikes(getLikesByPostId(post.getId()));
                result.add(post);

            }
        }
        return result;
    }
    public ArrayList<Post> getPostsByUsernameId (int username_id, int viewerId){
        ArrayList<Post> result = new ArrayList<Post>();
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from posts where username_id = ? order by id desc", new String[]{Integer.toString(username_id)});
        if (c1.getCount() > 0) {
            c1.moveToFirst();
            Post post = new Post();
            int i = c1.getColumnIndex("id");
            post.setId(c1.getInt(i));
            i = c1.getColumnIndex("img");
            byte[] b = c1.getBlob(i);
            Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);
            post.setImage(img);
            i = c1.getColumnIndex("description");
            post.setDescription(c1.getString(i));
            i = c1.getColumnIndex("date");
            post.setDate(c1.getString(i));
            i = c1.getColumnIndex("like_count");
            post.setLikes(c1.getInt(i));
            i = c1.getColumnIndex("username_id");
            post.setUser(getUserById(c1.getInt(i)));
            post.setSelfLike(isLikedByUserId(viewerId, post.getId()));
            post.setLikes(getLikesByPostId(post.getId()));
            result.add(post);

            while(c1.moveToNext()){
                post = new Post();
                i = c1.getColumnIndex("id");
                post.setId(c1.getInt(i));
                i = c1.getColumnIndex("img");
                b = c1.getBlob(i);
                img = BitmapFactory.decodeByteArray(b, 0, b.length);
                post.setImage(img);
                i = c1.getColumnIndex("description");
                post.setDescription(c1.getString(i));
                i = c1.getColumnIndex("date");
                post.setDate(c1.getString(i));
                i = c1.getColumnIndex("like_count");
                post.setLikes(c1.getInt(i));
                i = c1.getColumnIndex("username_id");
                post.setUser(getUserById(c1.getInt(i)));
                post.setSelfLike(isLikedByUserId(viewerId, post.getId()));
                post.setLikes(getLikesByPostId(post.getId()));
                result.add(post);

            }
        }
        return result;
    }
    public Boolean deletePost(int id){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        long result = MyDatabase.delete("posts", "id = ?", new String[]{Integer.toString(id)});
        if (result == -1){
            return false;
        }
        return true;
    }
    public Boolean updatePostDescription(int id, String newDescription){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", newDescription);
        int i = db.update("posts", values, "id = ?", new String[]{Integer.toString(id)});
        if (i != 1){
            return false;
        }
        return true;
    }
    //LIKES
    public int getLikesByPostId(int postId){
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from likes where post_id = ?", new String[]{Integer.toString(postId)});
        return c1.getCount();
    }
    public Boolean insertLike(int userId, int postId){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userId);
        contentValues.put("post_id", postId);
        long result = MyDatabase.insert("likes", null, contentValues);
        if(result == -1){
            return false;
        }
        return true;
    }
    public void deleteLike(int userId, int postId){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        MyDatabase.delete("likes", "user_id = ? and post_id = ?", new String[]{Integer.toString(userId), Integer.toString(postId)});
    }
    public Boolean isLikedByUserId(int userId, int postId){
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from likes where post_id = ? and user_id = ?", new String[]{Integer.toString(postId), Integer.toString(userId)});
        if(c1.getCount() > 0){
            return true;
        }

        return false;
    }


    //COMMENTS
    public ArrayList<Comment> getCommentsByPostId(int postId){
        ArrayList<Comment> result = new ArrayList<Comment>();
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from comments where post_id = ? order by id desc", new String[]{Integer.toString(postId)});
        if (c1.getCount() > 0) {
            c1.moveToFirst();
            Comment comment = new Comment();
            int i = c1.getColumnIndex("id");
            comment.setId(c1.getInt(i));
            i = c1.getColumnIndex("text");
            comment.setText(c1.getString(i));
            i = c1.getColumnIndex("user_id");
            User user = getUserById(c1.getInt(i));
            comment.setUser(user);
            result.add(comment);

            while(c1.moveToNext()){
                comment = new Comment();
                i = c1.getColumnIndex("id");
                comment.setId(c1.getInt(i));
                i = c1.getColumnIndex("text");
                comment.setText(c1.getString(i));
                i = c1.getColumnIndex("user_id");
                user = getUserById(c1.getInt(i));
                comment.setUser(user);
                result.add(comment);
            }
        }
        return result;

    }
    public int getCommentsCountByPostId(int postId){
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor c1 = MyDatabase.rawQuery("Select * from comments where post_id = ?", new String[]{Integer.toString(postId)});
        return c1.getCount();
    }
    public Comment insertComment(int userId, int postId, String text){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userId);
        contentValues.put("post_id", postId);
        contentValues.put("text", text);
        long r = MyDatabase.insert("comments", null, contentValues);
        User user = getUserById(userId);
        Comment c = new Comment();
        c.setUser(user);
        c.setId((int) r);
        c.setText(text);
        return c;
    }
    public Boolean deleteComment(int id){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        long result = MyDatabase.delete("comments", "id = ?", new String[]{Integer.toString(id)});
        if (result == -1){
            return false;
        }
        return true;
    }
}