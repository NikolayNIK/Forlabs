package ru.kollad.forlabs.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.kollad.forlabs.R;
import ru.kollad.forlabs.model.Attachment;
import ru.kollad.forlabs.model.Message;

/**
 * Created by NikolayNIK on 18.11.2018.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

	private static final int ATTACHMENT_RECYCLE_POOL_SIZE = 4;

	private final Context context;
	private final Stack<View> attachmentViewPool = new Stack<>();

	private List<Message> messages;

	public MessageAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getItemCount() {
		return messages == null ? 0 : messages.size();
	}

	private View getAttachmentView(ViewGroup container) {
		if (attachmentViewPool.empty()) {
			return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.item_attachment, container, false);
		}

		return attachmentViewPool.pop();
	}

	public void setMessages(List<Message> messages) {
		if (this.messages != null) notifyItemRangeRemoved(0, this.messages.size());
		this.messages = messages;
		if (messages != null) notifyItemRangeInserted(0, messages.size());
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.item_message, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Message item = messages.get(position);
		holder.textName.setText(item.getUserName());
		holder.textTime.setText(item.getCreatedAt().toString());
		holder.textMessage.setText(item.getMessage());
		holder.textMessage.setVisibility(TextUtils.isEmpty(item.getMessage()) ? View.GONE : View.VISIBLE);

		RequestOptions requestOptions = new RequestOptions().circleCrop();
		for (Attachment attachment : item.getAttachments()) {
			View view = getAttachmentView(holder.layoutAttachments);
			view.setOnClickListener(new OnAttachmentClickListener(attachment));
			((TextView) view.findViewById(R.id.text_name)).setText(attachment.getFileName());
			((TextView) view.findViewById(R.id.text_size)).setText(attachment.getHumanFileSize());
			Glide.with(context).load(attachment.getPreviewUrl())
					.apply(requestOptions).into((ImageView) view.findViewById(R.id.image_thumbnail));

			holder.layoutAttachments.addView(view);
		}
	}

	@Override
	public void onViewRecycled(@NonNull ViewHolder holder) {
		super.onViewRecycled(holder);

		for (int i = 0; i < holder.layoutAttachments.getChildCount(); i++) {
			if (attachmentViewPool.size() >= ATTACHMENT_RECYCLE_POOL_SIZE) break;
			attachmentViewPool.add(holder.layoutAttachments.getChildAt(i));
		}

		holder.layoutAttachments.removeAllViews();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView textName, textTime, textMessage;
		private final LinearLayout layoutAttachments;

		private ViewHolder(@NonNull View itemView) {
			super(itemView);

			textName = itemView.findViewById(R.id.text_name);
			textTime = itemView.findViewById(R.id.text_time);
			textMessage = itemView.findViewById(R.id.text_message);
			layoutAttachments = itemView.findViewById(R.id.layout_attachments);
		}
	}

	private class OnAttachmentClickListener implements View.OnClickListener {

		private final Attachment attachment;

		private OnAttachmentClickListener(Attachment attachment) {
			this.attachment = attachment;
		}

		@Override
		public void onClick(View v) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(attachment.getUrl())));
		}
	}
}
