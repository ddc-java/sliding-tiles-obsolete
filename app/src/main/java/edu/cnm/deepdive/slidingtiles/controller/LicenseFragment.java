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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import edu.cnm.deepdive.slidingtiles.BuildConfig;
import edu.cnm.deepdive.slidingtiles.R;

public class LicenseFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_license, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupActionBar();
    setupUI(view);
  }

  @SuppressWarnings("ConstantConditions")
  private void setupActionBar() {
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);
  }

  @SuppressLint({"SetJavaScriptEnabled"})
  private void setupUI(View root) {
    WebView content = root.findViewById(R.id.content);
    content.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri requestUri = request.getUrl();
        if (requestUri.equals(Uri.parse(BuildConfig.LICENSE_URL))) {
          return false;
        } else {
          Intent intent = new Intent(Intent.ACTION_VIEW, requestUri);
          //noinspection ConstantConditions
          getActivity().startActivity(intent);
          return true;
        }
      }
    });
    content.loadUrl(BuildConfig.LICENSE_URL);
    OssLicensesMenuActivity.setActivityTitle(getString(R.string.title_license));
    //noinspection ConstantConditions
    root.findViewById(R.id.more).setOnClickListener((v) ->
        getActivity().startActivity(new Intent(getContext(), OssLicensesMenuActivity.class)));
  }

}
