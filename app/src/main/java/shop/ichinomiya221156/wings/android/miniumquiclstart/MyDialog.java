package shop.ichinomiya221156.wings.android.miniumquiclstart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {
    boolean pressedYes;
    String SpreadSheetid;
    String name;
    Context context;

    public MyDialog(Context context, String name, String id) {
        this.SpreadSheetid = id;
        this.name = name;
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("注意")
                .setMessage("["+name+"」が編集される可能性があります。ファイルが間違っていないか確認してください。")
                .setNegativeButton("戻る", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setPositiveButton("続ける", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(context,SpreadSheetEditorActivity.class);
                        i.putExtra("name",name);
                        i.putExtra("id",SpreadSheetid);
                        context.startActivity(i);
                    }
                });

        return builder.create();
    }
}
