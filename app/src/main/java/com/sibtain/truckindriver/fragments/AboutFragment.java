package com.sibtain.truckindriver.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shashank.sony.fancyaboutpagelib.FancyAboutPage;
import com.sibtain.truckindriver.R;


public class AboutFragment extends Fragment {





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        FancyAboutPage fancyAboutPage=v.findViewById(R.id.fancyboutpage);
        //fancyAboutPage.setCoverTintColor(Color.BLUE);  //Optional
        fancyAboutPage.setCover(R.drawable.tuckinlogo); //Pass your cover image

        fancyAboutPage.setName("ARSALAN WAZIR");
        fancyAboutPage.setDescription("Google Certified Associate Android Developer | Android App, Game, Web and Software Developer.");
        fancyAboutPage.setAppIcon(R.drawable.tuckinlogo); //Pass your app icon image
        fancyAboutPage.setAppName("Truck In");
        fancyAboutPage.setVersionNameAsAppSubTitle("1.0.3");
        fancyAboutPage.setAppDescription("Cake Pop Icon Pack is an icon pack which follows Google's Material Design language.\n\n" +
                "This icon pack uses the material design color palette given by google. Every icon is handcrafted with attention to the smallest details!\n");
        fancyAboutPage.addEmailLink("arsalsami48@gmail.com");     //Add your email id
        fancyAboutPage.addFacebookLink("https://www.facebook.com/TruckIN");  //Add your facebook address url
        fancyAboutPage.addTwitterLink("https://twitter.com/TruckIN");
        fancyAboutPage.addLinkedinLink("https://www.linkedin.com/in/Truckin");
        fancyAboutPage.addGitHubLink("https://github.com/arsal");
        fancyAboutPage.setPadding(0,0,0,20);
        return v;
    }
}