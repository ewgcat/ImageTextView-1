package com.forum.harsh.forumpoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final String TEXT = "Just when I was starting to think that nobody was ever going " +
            "to make some new aftermarket parts for the Jeep Renegade, I came across a company company " +
            "called Motor City and they came to show off this nice Latitude at the 2017 SEMA show. In " +
            "addition to having a nice front bumper, light bar and cool tire carrier that was integrated " +
            "into the hatch back, they also had awesome new rock doors as well. If you're a fan of the " +
            "Renegade or not, this thing was worth giving a look." +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174442-23cebbe5-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174456-f7c9ce35-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174527-dcfa9d44-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174500-4d6cdcaa-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174633-0d7fe027-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174638-df78f0a2-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174536-5f4e9260-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174540-83298989-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174545-24b8a6ea-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174549-f27217c3-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174553-eb033a8e-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174557-a8d655ca-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174601-bfdce96f-me.jpg[/IMG]" +
            "\n\n[IMG]https://www.wayalife.com/photos/_data/i/upload/2017/10/31/20171031174624-64484ea6-me.jpg[/IMG]";
    private ImageTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView.setText(TEXT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textView.cancelNetworkCalls();
    }
}
