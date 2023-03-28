package com.example.myphotoeditor.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myphotoeditor.R;
import java.util.ArrayList;
import java.util.List;

public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {
    private OnItemSelected mOnItemSelected;
    private List<ToolModel> mToolList;

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
        mToolList = new ArrayList<>();

        // добавлять ли?   mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel("Text", R.drawable.ic_tune, ToolType.TUNE));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_correction, ToolType.CORRECTION));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_retouch, ToolType.RETOUCH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.getToolName());
        holder.imgToolIcon.setImageResource(item.getToolIcon());
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        public ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).getToolType());
                }
            });
        }
    }

    public class ToolModel {
        private String mToolName;
        private int mToolIcon;
        private ToolType mToolType;

        public ToolModel(String toolName, int toolIcon, ToolType toolType) {
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }

        public String getToolName() {
            return mToolName;
        }

        public int getToolIcon() {
            return mToolIcon;
        }

        public ToolType getToolType() {
            return mToolType;
        }
    }
}
