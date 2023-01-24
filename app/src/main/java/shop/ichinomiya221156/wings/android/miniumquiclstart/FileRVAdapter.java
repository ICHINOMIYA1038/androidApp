package shop.ichinomiya221156.wings.android.miniumquiclstart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FileRVAdapter extends RecyclerView.Adapter<FileRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FileModel> fileArrayList;

    public FileRVAdapter(Context context, ArrayList<FileModel> fileArrayList) {
        this.context = context;
        this.fileArrayList = fileArrayList;
    }

    @NonNull
    @Override
    public FileRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fileitem,parent,false);
        return new FileRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileRVAdapter.ViewHolder holder, int position) {
        FileModel file = fileArrayList.get(position);
        holder.fileItem.setText(fileArrayList.get(position).getFileName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog dialog = new MyDialog(context,file.getFileName(),file.getid());
                dialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "my_dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView fileItem;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            fileItem = itemView.findViewById(R.id.idFileTV);

        }
    }
}
