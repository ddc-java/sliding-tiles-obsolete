package edu.cnm.deepdive.slidingtiles.controller;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import edu.cnm.deepdive.slidingtiles.R;
import java.util.LinkedHashSet;
import java.util.Set;

public class PermissionsFragment extends DialogFragment {

  private static final String PERMISSIONS_TO_EXPLAIN_KEY = "permissions_to_explain";
  private static final String PERMISSIONS_TO_REQUEST_KEY = "permissions_to_request";
  private static final String EXPLANATION_KEY_SUFFIX = "_explanation";
  private static final String PERMISSION_DELIMITER = "\\.";

  @NonNull
  public static PermissionsFragment createInstance(@NonNull String[] permissionsToExplain,
      String[] permissionsToRequest) {
    if (permissionsToExplain.length == 0) {
      throw new IllegalArgumentException();
    }
    Bundle args = new Bundle();
    args.putStringArray(PERMISSIONS_TO_EXPLAIN_KEY, permissionsToExplain);
    args.putStringArray(PERMISSIONS_TO_REQUEST_KEY, permissionsToRequest);
    PermissionsFragment fragment = new PermissionsFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    OnAcknowledgeListener listener = getListener();
    Bundle args = getArguments();
    String[] permissionsToExplain = args.containsKey(PERMISSIONS_TO_EXPLAIN_KEY)
        ? args.getStringArray(PERMISSIONS_TO_EXPLAIN_KEY)
        : new String[0];
    String[] permissionsToRequest = args.containsKey(PERMISSIONS_TO_REQUEST_KEY)
        ? args.getStringArray(PERMISSIONS_TO_REQUEST_KEY)
        : new String[0];
    return new Builder(getContext())
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.permissions_title)
        .setMessage(buildMessage(permissionsToExplain))
        .setNeutralButton(android.R.string.ok,
            (dlg, which) -> listener.onAcknowledge(permissionsToRequest))
        .create();
  }

  private OnAcknowledgeListener getListener() {
    OnAcknowledgeListener listener;
    Fragment parentFragment = getParentFragment();
    FragmentActivity hostActivity = getActivity();
    if (parentFragment instanceof OnAcknowledgeListener) {
      listener = (OnAcknowledgeListener) parentFragment;
    } else if (hostActivity instanceof OnAcknowledgeListener) {
      listener = (OnAcknowledgeListener) hostActivity;
    } else {
      listener = (perms) -> {};
    }
    return listener;
  }

  private String buildMessage(String[] permissionsToExplain) {
    String packageName = getContext().getPackageName();
    Resources res = getResources();
    Set<String> explanations = new LinkedHashSet<>();
    for (String permission : permissionsToExplain) {
      String[] permissionNameParts = permission.split(PERMISSION_DELIMITER);
      String permissionKey = permissionNameParts[permissionNameParts.length - 1].toLowerCase()
          + EXPLANATION_KEY_SUFFIX;
      int explanationId = res.getIdentifier(permissionKey, "string", packageName);
      if (explanationId != 0) {
        explanations.add(getString(explanationId));
      }
    }
    StringBuilder builder = new StringBuilder();
    for (String explanation : explanations) {
      builder.append(explanation);
      builder.append("\n");
    }
    return (builder.length() > 0) ? builder.substring(0, builder.length() - 1) : "";
  }

  public interface OnAcknowledgeListener {

    void onAcknowledge(String[] permissionToRequest);

  }

}
