package edu.cnm.deepdive.slidingtiles.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.service.GoogleSignInService;

public class MainActivity extends AppCompatActivity {

  private GoogleSignInService signInService;
  private NavController navController;
  private NavOptions childNavOptions;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.settings:
        openSettings();
        break;
      case R.id.sign_out:
        signInService.signOut()
            .addOnCompleteListener((ignore) -> {
              switchToLogin();
            });
        break;
      case R.id.scoreboard:
        openScoreboard();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  private void setupPersonalization() {
    signInService = GoogleSignInService.getInstance();
    signInService.getAccount().observe(this, (account) -> {/* TODO personalize display */});
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

  private void switchToLogin() {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private void openSettings() {
    navController.navigate(R.id.navigation_settings, null, childNavOptions);
  }

  private void openScoreboard() {
    navController.navigate(R.id.navigation_scoreboard, null, childNavOptions);
  }

}
