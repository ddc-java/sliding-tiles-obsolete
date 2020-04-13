package edu.cnm.deepdive.slidingtiles.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashSet;
import java.util.Set;

public class PermissionViewModel extends ViewModel {

  private final MutableLiveData<Set<String>> permissions = new MutableLiveData<>(new HashSet<>());

  public LiveData<Set<String>> getPermissions() {
    return permissions;
  }

  public void grantPermission(String permission) {
    Set<String> permissions = this.permissions.getValue();
    //noinspection ConstantConditions
    if (permissions.add(permission)) {
      this.permissions.setValue(permissions);
    }
  }

  public void revokePermission(String permission) {
    Set<String> permissions = this.permissions.getValue();
    //noinspection ConstantConditions
    if (permissions.remove(permission)) {
      this.permissions.setValue(permissions);
    }
  }

}
