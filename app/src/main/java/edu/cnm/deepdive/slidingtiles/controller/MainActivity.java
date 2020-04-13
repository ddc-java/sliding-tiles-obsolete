/*
 *  Copyright 2020 Deep Dive Coding/CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.slidingtiles.controller;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.controller.PermissionsFragment.OnAcknowledgeListener;
import edu.cnm.deepdive.slidingtiles.service.GoogleSignInService;
import edu.cnm.deepdive.slidingtiles.viewmodel.PermissionViewModel;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO Complete Javadocs.
 *
 * @author Nicholas Bennett, Chris Hughes, Steven Z&uacute;&ntilde;iga
 */
public class MainActivity extends AppCompatActivity
    implements OnAcknowledgeListener {

  private static final int PERMISSIONS_REQUEST_CODE = 1000;

  private GoogleSignInService signInService;
  private NavController navController;
  private NavOptions childNavOptions;
  private GoogleSignInAccount account;
  private PermissionViewModel viewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(PermissionViewModel.class);
    checkPermissions();
    setContentView(R.layout.activity_main);
    setupPersonalization();
    setupNavigation();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    // TODO Re-enable after scoreboard is implemented.
//    menu.findItem(R.id.sign_out).setVisible(account != null);
//    menu.findItem(R.id.sign_in).setVisible(account == null);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.scoreboard:
        openChildFragment(R.id.navigation_scoreboard);
        break;
      case R.id.sign_out:
        signInService.signOut();
        break;
      case R.id.sign_in:
        switchToLogin();
        break;
      case R.id.license_info:
        openChildFragment(R.id.navigation_license);
        break;
      case R.id.settings:
        openChildFragment(R.id.navigation_settings);
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_CODE) {
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int result = grantResults[i];
        if (result == PackageManager.PERMISSION_GRANTED) {
          viewModel.grantPermission(permission);
        } else {
          viewModel.revokePermission(permission);
        }
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override
  public void onAcknowledge(String[] permissionsToRequest) {
    ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSIONS_REQUEST_CODE);
  }

  private void checkPermissions() {
    try {
      PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
          PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
      String[] permissions = info.requestedPermissions;
      List<String> permissionsToRequest = new LinkedList<>();
      List<String> permissionsToExplain = new LinkedList<>();
      for (String permission : permissions) {
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          permissionsToRequest.add(permission);
          if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            permissionsToExplain.add(permission);
          }
        } else {
          viewModel.grantPermission(permission);
        }
      }
      if (!permissionsToExplain.isEmpty()) {
        explainPermissions(
            permissionsToExplain.toArray(new String[0]),
            permissionsToRequest.toArray(new String[0]));
      } else if (!permissionsToRequest.isEmpty()) {
        onAcknowledge(permissionsToRequest.toArray(new String[0]));
      }
    } catch (NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void setupPersonalization() {
    signInService = GoogleSignInService.getInstance();
    signInService.getAccount().observe(this, (account) -> {
      if (this.account == null && account != null) {
//        invalidateOptionsMenu(); // TODO Re-enable after scoreboard feature is implemented.
      } else if (this.account != null && account == null) {
        switchToLogin();
      }
      this.account = account;
      // TODO Perform additional personalization of UI as necessary.
    });
  }

  private void setupNavigation() {
    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        R.id.navigation_play, R.id.navigation_settings)
        .build();
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    childNavOptions = new NavOptions.Builder()
        .setPopUpTo(R.id.navigation_play, false)
        .build();
    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
  }

  private void explainPermissions(String[] permissionsToExplain, String[] permissionsToRequest) {
    PermissionsFragment fragment =
        PermissionsFragment.createInstance(permissionsToExplain, permissionsToRequest);
    fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
  }

  private void openChildFragment(int fragmentId) {
    navController.navigate(fragmentId, null, childNavOptions);
  }

  private void switchToLogin() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

}
