package edu.cnm.deepdive.slidingtiles.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.slidingtiles.R;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class GalleryAdapter extends ArrayAdapter<String> {

  private static final int LAYOUT_RESOURCE_ID = R.layout.item_gallery;

  private final Pattern imageSpecDelimiter;
  private final String imageSpec;
  private final Picasso picasso;

  public GalleryAdapter(
      @NonNull Context context, @NonNull List<String> objects, String imageSpec) {
    super(context, LAYOUT_RESOURCE_ID, objects);
    this.imageSpec = imageSpec;
    imageSpecDelimiter = Pattern.compile(context.getString(R.string.image_spec_delimiter));
    picasso = Picasso.get();
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    Context context = getContext();
    View root = (convertView != null) ? convertView
        : LayoutInflater.from(context).inflate(LAYOUT_RESOURCE_ID, parent, false);
    ImageView thumbnail = root.findViewById(R.id.thumbnail);
    TextView name = root.findViewById(R.id.name);
    String imageSpec = getItem(position);
    @SuppressWarnings("ConstantConditions")
    String[] parts = imageSpecDelimiter.split(imageSpec);
    String displayName = parts[0];
    String protocol = (parts.length > 1) ? parts[1] : null;
    String identifier = (parts.length > 2) ? parts[2] : null;
    name.setText(displayName);
    thumbnail.setContentDescription(displayName);
    if (Objects.equals(protocol, context.getString(R.string.image_resource_tag))) {
      int id = context.getResources()
          .getIdentifier(identifier, "drawable", context.getPackageName());
      picasso.load(id).centerCrop().resize(200, 200).into(thumbnail);
    } else if (Objects.equals(protocol, context.getString(R.string.image_uri_tag))) {
      picasso.load(Uri.parse(identifier)).centerCrop().resize(200, 200).into(thumbnail);
    } else {
      picasso.load(R.drawable.ic_photo_library).into(thumbnail);
    }
    root.setBackgroundColor(Objects.equals(imageSpec, this.imageSpec)
        ? ContextCompat.getColor(context, R.color.selectedImage)
        : ContextCompat.getColor(context, R.color.unselectedImage));
    return root;
  }

}
