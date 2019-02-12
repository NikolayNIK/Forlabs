package ru.kollad.forlabs.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Semester;
import ru.kollad.forlabs.model.Study;

/**
 * Created by NikolayNIK on 12.11.2018.
 */
public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.ViewHolder> {

	private final Context context;

	private Semester studies;

	private OnItemClickListener onItemClickListener;

	public SemesterAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getItemCount() {
		return studies == null ? 0 : studies.size();
	}

	public void setSemester(Semester studies) {
		if (this.studies != null) notifyItemRangeRemoved(0, this.studies.size());
		this.studies = studies;
		if (this.studies != null) notifyItemRangeInserted(0, this.studies.size());
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_study, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Study item = holder.item = studies.get(position);

		holder.textName.setText(item.getName());
		holder.textScore.setText(context.getString(R.string.text_score, item.getPoints()));
		holder.textTeacher.setText(item.getSimpleTeacherName());
		switch (item.getStatus()) {
			case Study.STATUS_NORMAL:
				holder.imageStatus.setVisibility(View.GONE);
				break;
			case Study.STATUS_CERTIFIED:
				holder.imageStatus.setVisibility(View.VISIBLE);
				holder.imageStatus.setImageResource(R.drawable.ic_thumb_up_accent_24dp);
				break;
			case Study.STATUS_DEBT:
				holder.imageStatus.setVisibility(View.VISIBLE);
				holder.imageStatus.setImageResource(R.drawable.ic_warning_accent_24dp);
				break;
			case Study.STATUS_COMPLETE:
				holder.imageStatus.setVisibility(View.VISIBLE);
				holder.imageStatus.setImageResource(R.drawable.ic_check_circle_accent_24dp);
				break;
		}
	}

	public interface OnItemClickListener {

		void onClickItemStudy(SemesterAdapter adapter, Study item);
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private final TextView textName, textScore, textTeacher;
		private final ImageView imageStatus;

		private Study item;

		private ViewHolder(@NonNull View itemView) {
			super(itemView);

			textName = itemView.findViewById(R.id.text_name);
			textScore = itemView.findViewById(R.id.text_score);
			textTeacher = itemView.findViewById(R.id.text_teacher);
			imageStatus = itemView.findViewById(R.id.image_status);

			itemView.findViewById(R.id.content).setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (onItemClickListener != null)
				onItemClickListener.onClickItemStudy(SemesterAdapter.this, item);
		}
	}
}
