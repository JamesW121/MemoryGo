package CornellTechStudio.cornell.cm.JamesW.memorygo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.PostViewHolder> {

    static class Post {
        String email;
        String ID;
        int like_number;
        MainActivity.Category category;
        Bitmap pic;


        public Post(String email, MainActivity.Category category, String ID, int like_number, Bitmap pic) {
            this.email = email;
            this.category = category;
            this.ID = ID;
            this.like_number = like_number;
            this.pic = pic;
        }
    }

    List<Post> postList;

    Context context;
    PostViewAdapter(List<Post> list){
        postList = list;
        Log.d("pva", "Created Adapter.");
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_viewcard, parent, false);
        context = view.getContext();
        PostViewHolder holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final int pos = position;
        holder.postCategory.setText(MainActivity.Category.toString(postList.get(position).category));
        Bitmap resizedbitmap1;
        //resizedbitmap1=Bitmap.createBitmap(postList.get(position).pic, 0,0,370, 200);

        Bitmap srcBmp = postList.get(position).pic;
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/3,
                    srcBmp.getHeight()/2,
                    400,
                    200
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2,
                    srcBmp.getHeight()/3,
                    400,
                    200
            );
        }

        holder.img.setImageBitmap(dstBmp);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = postList.get(pos).ID;
                Intent intent;
                intent = new Intent(context, PostShowActivity.class);
                intent.putExtra("ID", ID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView postCategory;
        TextView postEmail;
        ImageView img;
        //TextView postLocation;
        //TextView postTitle;
        TextView postLike;

        public PostViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            postCategory = itemView.findViewById(R.id.post_category);
            img = (ImageView)itemView.findViewById(R.id.imageView3);
        }
    }
}
