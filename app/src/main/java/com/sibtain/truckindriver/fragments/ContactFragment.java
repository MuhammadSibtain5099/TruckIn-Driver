package com.sibtain.truckindriver.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shashank.sony.fancyaboutpagelib.FancyAboutPage;
import com.sibtain.truckindriver.R;

public class ContactFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=  inflater.inflate(R.layout.fragment_contact, container, false);
        FancyAboutPage fancyAboutPage=v.findViewById(R.id.fancyboutpage);
        //fancyAboutPage.setCoverTintColor(Color.BLUE);  //Optional
        fancyAboutPage.setCover(R.drawable.tuckinlogo); //Pass your cover image

        fancyAboutPage.setName("ARSALAN WAZIR");
        fancyAboutPage.setDescription("Google Certified Associate Android Developer | Android App, Game, Web and Software Developer.");
      //  fancyAboutPage.setAppIcon(R.drawable.tuckinlogo); //Pass your app icon image
        fancyAboutPage.setAppDescription("");
        fancyAboutPage.setAppIcon(R.drawable.tuckinlogo);
        fancyAboutPage.setAppName("");
        fancyAboutPage.addEmailLink("arsalsami48@gmail.com");     //Add your email id
        fancyAboutPage.addFacebookLink("https://www.facebook.com/TruckIN");  //Add your facebook address url
        fancyAboutPage.addTwitterLink("https://twitter.com/TruckIN");
        fancyAboutPage.addLinkedinLink("https://www.linkedin.com/in/Truckin");
        fancyAboutPage.addGitHubLink("https://github.com/arsal");
        fancyAboutPage.setPadding(0,0,0,20);
    return v;
    }
}