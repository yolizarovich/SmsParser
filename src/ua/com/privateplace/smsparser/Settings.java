package ua.com.privateplace.smsparser;

//import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
//import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
  /* // Option names and default values
   private static final boolean OPT_MUSIC_DEF = true;
   private static final boolean OPT_HINTS_DEF = true;*/

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);
   }

   
   /* Get the current value of the music option 
   public static boolean getMusic(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.pref_music_key), OPT_MUSIC_DEF);
   }
   
   /** Get the current value of the hints option 
   public static boolean getHints(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.pref_hints_key), OPT_HINTS_DEF);
   }*/
   
}
