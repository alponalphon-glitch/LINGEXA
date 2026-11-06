package com.lingexa.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.json.*;

/** LINGEXA offline-first learning prototype. Progress is stored on-device. */
public class MainActivity extends Activity {
    private static final int DEEP=0xFF061B58, BLUE=0xFF087DFF, GOLD=0xFFFFD43B, WHITE=Color.WHITE;
    private static final int SKY=0xFF18C8FF, ROYAL=0xFF0755D9, NAVY=0xFF071A55, MINT=0xFF22D58A;
    private LinearLayout root, content;
    private TextToSpeech tts;
    private boolean ttsReady=false;
    private String lastSpokenPrompt="";
    private android.content.SharedPreferences prefs;
    private int xp, gems, streak, hearts, level, lessonNo, selectedTab, dailyDone, moduleLevel = 1, moduleOneStep = 0;
    private int moduleOneFeedback = 0, moduleOneSelected = -1;
    private int moduleTwoFeedback = 0, moduleTwoSelected = -1;
    private boolean module3DTheme = false;
    private String learnerName;
    private String profileAvatar = "🦉";
    private String profileImageUri = "";
    private ImageView profilePhotoView;
    private final String[] profileAvatars={"🦉","🦊","🐼","🐯","🦁","🐸","🐨","🐻","🐰","🐱","🐶","🦄","🐵","🐧","🦋","🐬","🐺","🦖","🐹","🐼‍❄️"};
    private final String[] levels={"A1 · Beginner","A2 · Elementary","B1 · Intermediate","B2 · Upper-Intermediate","C1 · Advanced","C2 · Proficient"};
    private final String[] lessonQuestions={"What is this?","Choose the correct sentence.","Complete the sentence: I ___ to school every day.","Choose the opposite of ‘hot’.","Translate: Good morning."};
    private final String[][] lessonOptions={{"Apple","Banana","Orange","Pear"},{"She are a student.","She is a student.","She am a student.","She be a student."},{"go","goes","going","gone"},{"Warm","Cold","Fast","Tall"},{"Good night","Good morning","Goodbye","Thank you"}};
    private final int[] lessonAnswers={0,1,0,1,1};
    private final String[] vocab={"Apple","Book","Family","Travel","Beautiful","Knowledge","Friend","Water"};
    private final String[] vocabSpeak={"Apple","Book","Family","Travel","Beautiful","Knowledge","Friend","Water"};
    // Guided speaking curriculum: CEFR A1-C2, ten speaking missions per stage.
    private final String[][] speakingPrompts={
      {"Hello! My name is Anna.","I am from Canada.","I live in a small town.","I speak a little English.","How are you today?","I am happy to meet you.","I like music and films.","I have a brother and a sister.","Can you help me, please?","Thank you. Have a nice day!"},
      {"Yesterday I visited my friend.","I usually get up at seven.","I would like a cup of tea.","There are two shops near my home.","I am going to study tonight.","Could you tell me the way?","I have never been to London.","My favourite season is spring.","I was reading when you called.","Would you mind speaking slowly?"},
      {"I have been learning English for a year.","In my opinion, exercise improves health.","If I had more time, I would travel.","The meeting was postponed until Friday.","I am interested in learning new skills.","Although it was raining, we went out.","Could you explain what you mean?","One advantage is that it saves time.","I used to be shy when speaking.","I would recommend this book to beginners."},
      {"I would appreciate it if you could clarify the schedule.","The main reason for this change is efficiency.","I partly agree, although there are exceptions.","We need to consider the long-term consequences.","From my perspective, communication is essential.","The proposal needs further investigation.","I was under the impression that we had agreed.","Let me summarize the key points so far.","Despite the challenges, the project succeeded.","Could we explore an alternative approach?"},
      {"The findings suggest a significant shift in public attitudes.","Her argument is compelling, but it overlooks one factor.","We should distinguish between correlation and causation.","The issue is more nuanced than it initially appears.","I would welcome a more detailed explanation.","This decision may have unintended consequences.","The evidence does not fully support that assumption.","It is worth examining the broader implications.","I can see the rationale, even if I disagree.","Ultimately, the outcome depends on implementation."},
      {"A comprehensive solution requires balancing competing priorities.","The prevailing assumption deserves critical scrutiny.","We must account for the ambiguity in the available evidence.","Her contribution fundamentally reshaped the discussion.","The proposal is viable, provided that safeguards are introduced.","This interpretation is defensible, albeit not conclusive.","The distinction is subtle but conceptually important.","I would argue that the benefits outweigh the drawbacks.","The discussion revealed several underlying contradictions.","Let us reconsider the premise before reaching a conclusion."}
    };
    private final String[] speakingStages={"A1 · Beginner","A2 · Elementary","B1 · Intermediate","B2 · Upper-Intermediate","C1 · Advanced","C2 · Proficient"};
    private int speakingStage=0, speakingStep=0;

    // Module 1 curriculum: ten guided sentence steps in every level. Each unit has its own theme.
    private final String[][] m1Sentences={
      {"Hello! My name is Mia.","Good morning, everyone.","How are you today?","I am fine, thank you.","Nice to meet you.","See you tomorrow!","What is your name?","I am from Canada.","This is my friend, Leo.","Have a wonderful day!"},
      {"This is my mother.","I have two brothers.","My family is kind.","My sister is a student.","We live in a small house.","My father cooks dinner.","The baby is sleeping.","Our grandparents visit us.","I love spending time with them.","We are a happy family."},
      {"I like fresh apples.","Would you like some water?","The soup is hot.","I eat breakfast at seven.","She drinks orange juice.","We need two eggs.","This sandwich is delicious.","He does not eat meat.","Can I have the menu, please?","The bill, please."},
      {"This is my bedroom.","There is a lamp on the desk.","The keys are under the book.","Our kitchen is very clean.","The garden is behind the house.","I make my bed every morning.","There are three chairs here.","The window is open.","Please close the door.","My home feels comfortable."},
      {"I go to school by bus.","My teacher is very helpful.","We study English on Monday.","There are twenty students in my class.","I write in my notebook.","The lesson starts at nine.","Can you repeat that, please?","I finished my homework.","Our library is next to the hall.","Learning is an adventure."},
      {"I wake up at six.","She brushes her teeth.","We have lunch at noon.","He goes to work by train.","I usually walk after dinner.","They are watching television now.","I never forget my keys.","What time do you finish work?","I am getting ready for bed.","Every day is a new chance."},
      {"The bank is near the station.","Turn left at the traffic lights.","The museum opens at ten.","How can I get to the park?","Go straight for two blocks.","The supermarket is across the street.","This road is very busy.","We are waiting at the bus stop.","The café is between two shops.","The city looks beautiful at night."},
      {"I would like to book a room.","Where is the airport?","My flight leaves at noon.","Please show me your passport.","We are staying for three nights.","How much is a ticket?","The train is delayed.","I need help with my luggage.","Could you speak more slowly?","Have a safe journey!"},
      {"She plays tennis every weekend.","They visited us yesterday.","I am going to call him.","There are some books on the table.","He can speak three languages.","We were tired after the trip.","If it rains, we will stay home.","This exercise is easier than that one.","I have lived here for two years.","Practice makes progress."},
      {"I am proud of your progress.","Could you explain your idea?","Although it was late, we continued.","The solution was better than expected.","I have never seen such a view.","She suggested taking a short break.","By the time we arrived, it had started.","Learning a language takes patience.","Small habits create lasting results.","You are ready for the next adventure."}
    };
    private final String[][] m1Meanings={
      {"A friendly way to greet someone.","A greeting used before noon.","Ask about someone's well-being.","A polite positive reply.","Say this when meeting someone.","A friendly way to say goodbye.","Ask for a person's name.","Tell someone your country of origin.","Introduce another person.","Wish someone a good day."},
      {"Introduce your female parent.","Tell how many male siblings you have.","Describe the people in your family.","Talk about your female sibling.","Describe where your family lives.","Describe a regular action.","Say what the baby is doing now.","Talk about relatives visiting.","Say that you enjoy being together.","Describe your family positively."},
      {"Express a food preference.","Offer a drink politely.","Describe food temperature.","Talk about a daily meal.","Describe someone's drink habit.","Ask for ingredients.","Give a positive opinion about food.","Use 'does not' for a negative habit.","Make a polite restaurant request.","Ask to pay in a restaurant."},
      {"Name a room in a home.","Use 'there is' for one thing.","Use 'under' to show location.","Describe a room.","Use 'behind' for position.","Describe a daily routine.","Use 'there are' for plural things.","Describe the state of a window.","Make a polite instruction.","Describe how a place makes you feel."},
      {"Describe your way of travelling to school.","Describe a person who teaches you.","Use 'on' with days of the week.","Describe the number of classmates.","Describe a study action.","Talk about a scheduled start time.","Ask someone to say something again.","Use the past tense for completed work.","Describe where the library is.","Share a positive learning message."},
      {"Talk about your morning routine.","Describe a regular action by she.","Talk about a regular meal time.","Describe a commute.","Use usually for a habit.","Use present continuous for now.","Use never for something that does not happen.","Ask about finishing time.","Describe an action happening now.","Encourage a fresh start."},
      {"Describe where a place is.","Give a direction.","Talk about opening times.","Ask for directions politely.","Give a simple route.","Use across from to describe location.","Describe traffic.","Describe waiting for transport.","Use between for two sides.","Describe a city at night."},
      {"Make a polite hotel request.","Ask for a location.","Talk about a departure time.","A request at passport control.","Describe the length of a stay.","Ask about a price.","Describe a late train.","Ask for assistance.","Make a polite communication request.","Wish someone a good trip."},
      {"Use present simple for a habit.","Use past simple for a finished visit.","Use going to for a plan.","Use some with plural nouns.","Use can for ability.","Use were for a past state.","Use if with a possible future result.","Use an adjective comparison.","Use present perfect for duration until now.","A reminder that practice helps."},
      {"Use proud to express positive feeling.","Ask for clarification politely.","Use although to show contrast.","Describe an unexpected result.","Use present perfect for life experience.","Use suggest plus an -ing verb.","Use past perfect for an earlier past action.","Explain that learning needs time.","Describe how small routines help.","Celebrate readiness to continue."}
    };
    private final String[][] m1Options={
      {"Introduce yourself","Say goodbye","Order food","Ask the time"},
      {"Talk about family","Describe the weather","Ask for directions","Name a sport"},
      {"Talk about food","Talk about school","Describe a city","Talk about music"},
      {"Describe a home","Talk about travel","Describe a job","Talk about animals"},
      {"Talk about school","Order a meal","Describe a holiday","Ask for a taxi"},
      {"Describe a routine","Talk about a museum","Name a country","Order a ticket"},
      {"Ask for directions","Talk about feelings","Describe a meal","Talk about hobbies"},
      {"Travel English","Family English","Weather English","Sports English"},
      {"Review grammar","Name colors","Talk about pets","Order coffee"},
      {"Advanced communication","Basic greetings","Food vocabulary","Numbers"}
    };

    private final String[] featureNames={"Vocabulary","Grammar","Listening","Speaking","Writing","Challenges","League","Progress","AI Teacher"};
    private final String[] featureIcons={"📚","🧩","🎧","🎙️","✍️","🏆","🥇","📈","🦉"};

    @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setStatusBarColor(DEEP);getWindow().setNavigationBarColor(DEEP);prefs=getSharedPreferences("lingexa_progress",MODE_PRIVATE);loadState();tts=new TextToSpeech(this,status->{if(status==TextToSpeech.SUCCESS){ttsReady=true;tts.setSpeechRate(0.88f);tts.setPitch(1.04f);int ru=tts.setLanguage(new Locale("ru","RU"));if(ru==TextToSpeech.LANG_MISSING_DATA||ru==TextToSpeech.LANG_NOT_SUPPORTED){tts.setLanguage(Locale.US);}}});showHome();}
    private void loadState(){xp=prefs.getInt("xp",0);gems=prefs.getInt("gems",25);streak=prefs.getInt("streak",1);hearts=prefs.getInt("hearts",5);if(hearts<5&&!prefs.contains("heart_timer_anchor"))prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();level=prefs.getInt("level",0);lessonNo=prefs.getInt("lesson",0);dailyDone=prefs.getInt("daily",0);learnerName=prefs.getString("name","Learner");profileAvatar=prefs.getString("profile_avatar","🦉");profileImageUri=prefs.getString("profile_image_uri","");}
    private void saveState(){prefs.edit().putInt("xp",xp).putInt("gems",gems).putInt("streak",streak).putInt("hearts",hearts).putInt("level",level).putInt("lesson",lessonNo).putInt("daily",dailyDone).putString("name",learnerName).putString("profile_avatar",profileAvatar).putString("profile_image_uri",profileImageUri).apply();}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),0x44FFFFFF);return d;}
    private GradientDrawable gradient(int a,int b,int radius){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{a,b});d.setCornerRadius(dp(radius));d.setStroke(dp(1),0x55FFFFFF);return d;}
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private TextView txt(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setIncludeFontPadding(false);t.setFontFeatureSettings("kern");t.setLetterSpacing(size>=18?0.008f:0.003f);t.setTypeface(Typeface.create("sans-serif-rounded",bold?Typeface.BOLD:Typeface.NORMAL));return t;}
    private void enter(View v,long delay){v.setAlpha(0f);v.setTranslationY(dp(12));v.setScaleX(.985f);v.setScaleY(.985f);v.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).setStartDelay(delay).setDuration(260).setInterpolator(new android.view.animation.DecelerateInterpolator()).start();}
    private void press(View v){v.animate().cancel();v.animate().scaleX(.975f).scaleY(.975f).setDuration(75).withEndAction(()->v.animate().scaleX(1f).scaleY(1f).setDuration(125).start()).start();}
    private void depthTouch(View v){v.setOnTouchListener((view,event)->{if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){float nx=(event.getX()/Math.max(1f,view.getWidth())-.5f);float ny=(event.getY()/Math.max(1f,view.getHeight())-.5f);view.setRotationY(Math.max(-4f,Math.min(4f,nx*8f)));view.setRotationX(Math.max(-4f,Math.min(4f,-ny*8f)));view.setTranslationZ(dp(4));}else if(event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL){view.animate().rotationX(0f).rotationY(0f).translationZ(dp(1)).setDuration(180).start();}return false;});}
    private LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private void space(LinearLayout box,int h){View v=new View(this);box.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
    private void base(String title,int tab){selectedTab=tab;module3DTheme=title!=null && title.startsWith("Module ");ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);scroll.setVerticalScrollBarEnabled(false);scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);root=col();root.setPadding(dp(15),dp(10),dp(15),dp(18));root.setBackground(gradient(0xFF101D45,0xFF050B22,0));scroll.addView(root);setContentView(scroll);enter(root,0);
        LinearLayout header=row();header.setPadding(dp(3),dp(5),dp(3),dp(5));TextView brand=txt("LINGEXA ✦",25,WHITE,true);brand.setShadowLayer(dp(4),0,dp(2),0x99001855);header.addView(brand,new LinearLayout.LayoutParams(0,-2,1));TextView wallet=txt("💎 "+gems+"   ⭐ "+xp,14,WHITE,true);wallet.setPadding(dp(14),dp(10),dp(14),dp(10));wallet.setBackground(gradient(0xFF314E91,0xFF17264E,24));wallet.setElevation(dp(7));wallet.setTranslationZ(dp(2));header.addView(wallet);root.addView(header);space(root,12);
        if(title!=null){TextView h=txt(title,24,WHITE,true);h.setPadding(dp(3),dp(5),0,dp(5));if(module3DTheme){h.setBackground(gradient(0xFF426ED0,0xFF172B65,20));h.setPadding(dp(15),dp(13),dp(13),dp(13));h.setElevation(dp(8));h.setTranslationZ(dp(2));}root.addView(h);space(root,12);}content=col();root.addView(content);space(root,18);buildNav();}
    private void buildNav(){
        LinearLayout nav=row();nav.setPadding(dp(6),dp(8),dp(6),dp(8));nav.setBackground(gradient(0xFF263C70,0xFF101A38,25));nav.setElevation(dp(13));nav.setTranslationZ(dp(3));
        String[] icons={"🏡","📚","🔊","🎤","👤"};String[] labels={"Home","Learn","Sounds","Speaking","Profile"};
        int[][] colors={{0xFFFFD66B,0xFFE56A25},{0xFF8AF0C0,0xFF087B63},{0xFF9AE8FF,0xFF2468D8},{0xFFFFB1D1,0xFFCA397C},{0xFFD5C6FF,0xFF6847C8}};
        for(int i=0;i<icons.length;i++){final int x=i;LinearLayout item=col();item.setGravity(Gravity.CENTER);item.setPadding(dp(2),dp(4),dp(2),dp(4));if(i==selectedTab){item.setBackground(gradient(0xFF385FA8,0xFF1A3470,18));item.setElevation(dp(6));}
            TextView icon=txt(icons[i],25,WHITE,true);icon.setGravity(Gravity.CENTER);GradientDrawable badge=gradient(colors[i][0],colors[i][1],15);badge.setStroke(dp(1),0xCCFFFFFF);icon.setBackground(badge);icon.setElevation(dp(i==selectedTab?9:5));icon.setTranslationZ(dp(2));LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(40),dp(38));ip.gravity=Gravity.CENTER;item.addView(icon,ip);
            space(item,4);TextView label=txt(labels[i],10,i==selectedTab?0xFFFFE58A:0xFFD9E4FF,true);label.setGravity(Gravity.CENTER);item.addView(label);nav.addView(item,new LinearLayout.LayoutParams(0,dp(61),1));
            item.setOnClickListener(v->{press(icon);if(x==0)showHome();else if(x==1)showLearn();else if(x==2)showAlphabetSounds();else if(x==3)showSpeakingCourse();else showProfile();});
        }root.addView(nav);
    }
    private void showMainMenu(){base("Explore LINGEXA",-1);OwlMascotView owl=new OwlMascotView();LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(dp(132),dp(126));op.gravity=Gravity.CENTER;content.addView(owl,op);TextView welcome=txt("Your learning world",19,0xFFFFE77A,true);welcome.setGravity(Gravity.CENTER);content.addView(welcome);space(content,8);addButton("💎  Premium Membership",this::showPremium);addButton("👤  My Profile",this::showProfile);addButton("⚙️  Settings",this::showSettings);addButton("🦉  AI Teacher",this::showAiTeacher);}
    private void showPracticeMenu(){base("Practice Center",2);for(String n:new String[]{"Vocabulary","Grammar","Listening","Speaking","Writing","Challenges","AI Teacher"})addButton(n,()->openFeature(n));}
    private void showAlphabetSounds(){
        base("English Sounds",2);
        TextView intro=txt("Learn every letter. Listen and repeat with your owl teacher!",16,WHITE,true);
        intro.setGravity(Gravity.CENTER);card(intro,0xFF164FC4);
        OwlMascotView owl=new OwlMascotView();LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(dp(142),dp(132));op.gravity=Gravity.CENTER;content.addView(owl,op);
        TextView coach=txt("🦉  Повторяй за мной! Сначала слушай, затем произноси звук вслух.",14,0xFFFFE77A,true);coach.setGravity(Gravity.CENTER);card(coach,0xFF153C91);
        addButton("▶  Listen to all vowels",()->speakEnglish("A, apple. E, egg. I, igloo. O, orange. U, umbrella. These are the five English vowels."));
        heading("VOWELS  ·  ГЛАСНЫЕ");
        String[][] vowels={{"A","/æ/","apple"},{"E","/e/","egg"},{"I","/ɪ/","igloo"},{"O","/ɒ/","orange"},{"U","/ʌ/","umbrella"}};
        addSoundGrid(vowels,true);
        addButton("▶  Listen to all consonants",()->speakEnglish("B, book. C, cat. D, dog. F, fish. G, go. H, hat. J, jam. K, kite. L, lamp. M, moon. N, nose. P, pen. Q, queen. R, red. S, sun. T, top. V, van. W, water. X, box. Y, yes. Z, zoo."));
        heading("CONSONANTS  ·  СОГЛАСНЫЕ");
        String[][] consonants={{"B","/b/","book"},{"C","/k/","cat"},{"D","/d/","dog"},{"F","/f/","fish"},{"G","/g/","go"},{"H","/h/","hat"},{"J","/dʒ/","jam"},{"K","/k/","kite"},{"L","/l/","lamp"},{"M","/m/","moon"},{"N","/n/","nose"},{"P","/p/","pen"},{"Q","/kw/","queen"},{"R","/r/","red"},{"S","/s/","sun"},{"T","/t/","top"},{"V","/v/","van"},{"W","/w/","water"},{"X","/ks/","box"},{"Y","/j/","yes"},{"Z","/z/","zoo"}};
        addSoundGrid(consonants,false);
        TextView note=txt("Tap any letter to hear its name, sound and example word. Repeat aloud!",13,0xFFDCEBFF,false);note.setGravity(Gravity.CENTER);card(note,0xFF153C91);
    }
    private void addSoundGrid(String[][] sounds,boolean vowel){
        for(int i=0;i<sounds.length;i+=2){
            LinearLayout line=row();
            for(int j=i;j<Math.min(i+2,sounds.length);j++){
                final String letter=sounds[j][0],phoneme=sounds[j][1],example=sounds[j][2];
                TextView tile=txt(letter+"\n"+phoneme+"\n as in "+example+"  🔊",18,WHITE,true);tile.setGravity(Gravity.CENTER);tile.setPadding(dp(5),dp(13),dp(5),dp(13));tile.setBackground(gradient(vowel?0xFF13B95F:0xFF2589F5,vowel?0xFF087A49:0xFF123C9D,20));tile.setElevation(dp(5));
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(dp(4),dp(4),dp(4),dp(8));line.addView(tile,lp);
                tile.setOnClickListener(v->{speakEnglish("The letter "+letter+". Listen to the sound in "+example+". "+letter+", "+example+". Repeat after me.");});
            }
            content.addView(line,new LinearLayout.LayoutParams(-1,-2));
        }
    }
    private void card(View v,int color){
        GradientDrawable surface=gradient(color,blendWithNavy(color),module3DTheme?27:24);surface.setStroke(dp(1),0x66FFFFFF);
        v.setBackground(surface);v.setElevation(dp(module3DTheme?10:8));v.setTranslationZ(dp(1));v.setPadding(dp(16),dp(15),dp(16),dp(15));depthTouch(v);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(module3DTheme?16:13);content.addView(v,p);enter(v,Math.min(90,content.getChildCount()*18L));
    }
    private int blendWithNavy(int color){return android.graphics.Color.rgb((Color.red(color)*2+8)/3,(Color.green(color)*2+28)/3,(Color.blue(color)*2+92)/3);}
    private TextView button(String label){TextView b=txt(label,16,WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(17),dp(16),dp(17),dp(16));GradientDrawable bg=gradient(0xFF438DFA,0xFF1747A0,25);bg.setStroke(dp(1),0x88FFFFFF);b.setBackground(bg);b.setElevation(dp(7));b.setTranslationZ(dp(1));return b;}
    private void addButton(String label,Runnable action){TextView b=button(label);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(12);content.addView(b,p);enter(b,Math.min(120,content.getChildCount()*14L));b.setOnClickListener(v->{press(b);action.run();});}
    private void heading(String s){TextView t=txt(s,18,0xFFFFE58A,true);t.setPadding(dp(9),dp(13),dp(8),dp(12));if(module3DTheme){t.setBackground(gradient(0xFF355FAE,0xFF172B60,17));t.setElevation(dp(5));}content.addView(t);enter(t,Math.min(100,content.getChildCount()*12L));if(module3DTheme)space(content,5);
    }
    private void openFeature(String n){switch(n){case "Vocabulary":showVocabulary();break;case "Grammar":showGrammar();break;case "Listening":showListening();break;case "Speaking":showSpeakingCourse();break;case "Writing":showWriting();break;case "Challenges":showChallenges();break;case "League":showLeague();break;case "AI Teacher":showAiTeacher();break;default:showProgress();}}
    private void showHome(){
        base(null,0);
        // Premium-style welcome hero inspired by the supplied LINGEXA reference.
        LinearLayout hero=col(); hero.setGravity(Gravity.CENTER); hero.setPadding(dp(16),dp(16),dp(16),dp(18));
        hero.setBackground(gradient(0xFF36B9F3,0xFF153A91,30)); hero.setElevation(dp(12)); hero.setTranslationZ(dp(2));depthTouch(hero);
        TextView greeting=txt("YOUR ENGLISH ADVENTURE",13,0xFFE9F8FF,true); greeting.setGravity(Gravity.CENTER); hero.addView(greeting);
        OwlMascotView mascot=new OwlMascotView(); LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(dp(176),dp(164)); mp.gravity=Gravity.CENTER; mp.topMargin=dp(4); hero.addView(mascot,mp);
        TextView title=txt("Learn. Practice. Speak.",25,WHITE,true); title.setGravity(Gravity.CENTER); hero.addView(title);
        TextView sub=txt("Small steps today. Big progress tomorrow.",13,0xFFE7F5FF,false); sub.setGravity(Gravity.CENTER); hero.addView(sub);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2); hp.bottomMargin=dp(12); content.addView(hero,hp);enter(hero,35);

        LinearLayout stats=row(); stats.setPadding(dp(6),dp(9),dp(6),dp(9)); stats.setBackground(gradient(0xFF314E88,0xFF111E43,22));stats.setElevation(dp(6));
        String[] vals={"🔥  "+streak+" DAY STREAK","⭐  "+xp+" XP","❤️  "+hearts};
        for(String value:vals){TextView t=txt(value,11,WHITE,true);t.setGravity(Gravity.CENTER);stats.addView(t,new LinearLayout.LayoutParams(0,dp(42),1));}
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.bottomMargin=dp(14);content.addView(stats,sp);

        LinearLayout continueCard=col();continueCard.setPadding(dp(16),dp(14),dp(16),dp(14));continueCard.setBackground(gradient(0xFFFFE98A,0xFFE99A26,24));continueCard.setElevation(dp(9));continueCard.setTranslationZ(dp(2));
        TextView ct=txt("CONTINUE YOUR JOURNEY",12,0xFF654000,true);continueCard.addView(ct);
        TextView lesson=txt("Lesson "+(lessonNo+1)+"  •  "+levels[level],18,0xFF14265B,true);continueCard.addView(lesson);
        TextView go=txt("START LEARNING  ▶",15,0xFF14265B,true);go.setGravity(Gravity.RIGHT);continueCard.addView(go);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(16);content.addView(continueCard,cp);enter(continueCard,80);continueCard.setOnClickListener(v->{press(continueCard);showModuleOne();});

        heading("Explore your skills");
        int[] colors={0xFF19B95B,0xFF8A42E8,0xFFFF8A18,0xFF078FE8,0xFFFF3D91,0xFFFFA900,0xFF7139E8,0xFF12A96B,0xFF087DCE};
        for(int i=0;i<featureNames.length;i+=2){LinearLayout r=row();for(int j=i;j<Math.min(i+2,featureNames.length);j++){
            final String name=featureNames[j];LinearLayout tile=col();tile.setGravity(Gravity.CENTER);tile.setPadding(dp(8),dp(12),dp(8),dp(12));tile.setBackground(gradient(colors[j],0xFF172C5C,22));tile.setElevation(dp(8));tile.setTranslationZ(dp(1));depthTouch(tile);
            TextView ic=txt(featureIcons[j],34,WHITE,true);ic.setGravity(Gravity.CENTER);tile.addView(ic);
            TextView label=txt(name,15,WHITE,true);label.setGravity(Gravity.CENTER);tile.addView(label);
            LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,dp(104),1);tp.setMargins(dp(4),dp(4),dp(4),dp(4));r.addView(tile,tp);enter(tile,100+j*18L);tile.setOnClickListener(v->{press(tile);openFeature(name);});
        }LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.bottomMargin=dp(3);content.addView(r,rp);}

        LinearLayout premium=col();premium.setPadding(dp(16),dp(14),dp(16),dp(14));premium.setBackground(gradient(0xFFB56BFF,0xFF4B238E,24));premium.setElevation(dp(9));depthTouch(premium);
        TextView pt=txt("👑  LINGEXA PREMIUM",18,WHITE,true);premium.addView(pt);
        TextView ps=txt("Unlock every lesson, more practice and an ad-free experience.",13,0xFFF1E7FF,false);premium.addView(ps);
        TextView pb=txt("EXPLORE PREMIUM  →",14,0xFFFFF2A6,true);pb.setPadding(0,dp(8),0,0);premium.addView(pb);
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,-2);pp.topMargin=dp(12);content.addView(premium,pp);enter(premium,180);premium.setOnClickListener(v->{press(premium);showPremium();});
        addButton("👤  MY PROFILE",this::showProfile);
    }

    /** Lightweight animated 2.5D owl mascot. Drawn locally; no 3D engine or network assets. */
    private class OwlMascotView extends View {
        private final android.graphics.Paint p=new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        private final android.graphics.Paint line=new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        OwlMascotView(){super(MainActivity.this);setContentDescription("LINGEXA animated owl teacher");}
        private void oval(android.graphics.Canvas c,float l,float t,float r,float b,int top,int bottom){p.setStyle(android.graphics.Paint.Style.FILL);p.setShader(new android.graphics.LinearGradient(l,t,r,b,top,bottom,android.graphics.Shader.TileMode.CLAMP));c.drawOval(l,t,r,b,p);p.setShader(null);}
        private void circle(android.graphics.Canvas c,float x,float y,float r,int center,int edge){p.setShader(new android.graphics.RadialGradient(x-r*.25f,y-r*.3f,r,new int[]{center,edge},null,android.graphics.Shader.TileMode.CLAMP));c.drawCircle(x,y,r,p);p.setShader(null);}
        @Override protected void onDraw(android.graphics.Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();if(w<=0||h<=0)return;long now=System.currentTimeMillis();float bob=(float)Math.sin(now/520.0)*2.7f;float scale=Math.min(w/190f,h/180f);c.save();c.translate(w/2f,h/2f+3+bob);c.scale(scale,scale);
            // Soft contact shadow and rounded body with glossy highlight.
            p.setShader(new android.graphics.RadialGradient(0,79,72,new int[]{0x66020A24,0x00020A24},null,android.graphics.Shader.TileMode.CLAMP));c.drawOval(-70,68,70,94,p);p.setShader(null);
            oval(c,-69,-65,69,83,0xFF51B9FF,0xFF0645A9);oval(c,-55,-57,55,66,0xFF78D5FF,0xFF0C5BC5);
            // Ear tufts and feather crown.
            oval(c,-62,-82,-23,-30,0xFF5BC1FF,0xFF0752B7);oval(c,23,-82,62,-30,0xFF5BC1FF,0xFF0752B7);oval(c,-13,-92,12,-55,0xFF8AE0FF,0xFF1768D2);
            // Wings move gently with the breathing cycle.
            float flap=(float)Math.sin(now/330.0)*8f;c.save();c.rotate(flap,-62,20);oval(c,-91,-3,-48,63,0xFF39A8F3,0xFF073F9C);c.restore();c.save();c.rotate(-flap,62,20);oval(c,48,-3,91,63,0xFF39A8F3,0xFF073F9C);c.restore();
            // Face mask and expressive eyes.
            oval(c,-55,-49,0,21,0xFFFFFFFF,0xFFD8F1FF);oval(c,0,-49,55,21,0xFFFFFFFF,0xFFD8F1FF);
            boolean blink=(now%4700)<145;float eyeY=blink?3.5f:21f;oval(c,-48,-22-eyeY,-7,-22+eyeY,0xFF102247,0xFF07132F);oval(c,7,-22-eyeY,48,-22+eyeY,0xFF102247,0xFF07132F);
            if(!blink){float gaze=(float)Math.sin(now/1500.0)*2.4f;circle(c,-27+gaze,-22,15,0xFF56D5FF,0xFF0750B4);circle(c,27+gaze,-22,15,0xFF56D5FF,0xFF0750B4);circle(c,-27+gaze,-22,9,0xFF06152F,0xFF06152F);circle(c,27+gaze,-22,9,0xFF06152F,0xFF06152F);circle(c,-32+gaze,-29,5,WHITE,0xFFEAF8FF);circle(c,22+gaze,-29,5,WHITE,0xFFEAF8FF);}
            circle(c,-49,2,7,0xFFFFB6C5,0xFFE9789B);circle(c,49,2,7,0xFFFFB6C5,0xFFE9789B);
            p.setShader(new android.graphics.LinearGradient(-10,-4,10,17,0xFFFFE36A,0xFFEC7B13,android.graphics.Shader.TileMode.CLAMP));android.graphics.Path beak=new android.graphics.Path();beak.moveTo(-13,-2);beak.quadTo(0,-10,13,-2);beak.lineTo(0,18);beak.close();c.drawPath(beak,p);p.setShader(null);
            oval(c,-43,29,43,83,0xFFFFF7E8,0xFFB8D8F3);circle(c,-23,79,7,0xFFFFD36A,0xFFE58A24);circle(c,23,79,7,0xFFFFD36A,0xFFE58A24);
            // Small graduation cap with a metallic tassel.
            p.setShader(new android.graphics.LinearGradient(-70,-96,70,-40,0xFF263A70,0xFF09142F,android.graphics.Shader.TileMode.CLAMP));android.graphics.Path cap=new android.graphics.Path();cap.moveTo(-72,-61);cap.lineTo(0,-96);cap.lineTo(72,-61);cap.lineTo(0,-32);cap.close();c.drawPath(cap,p);p.setShader(null);oval(c,-38,-62,38,-52,0xFF324D88,0xFF101B3B);line.setColor(0xFFFFD85A);line.setStrokeWidth(4);c.drawLine(62,-61,69,-23,line);circle(c,69,-19,5,0xFFFFF1A6,0xFFEAA21E);
            // Specular glint creates a subtle toy-like 3D finish.
            circle(c,-48,-53,4,0xAAFFFFFF,0x00FFFFFF);circle(c,43,-52,3,0x88FFFFFF,0x00FFFFFF);
            c.restore();if(isShown())postInvalidateDelayed(34);
        }
        @Override protected void onDetachedFromWindow(){super.onDetachedFromWindow();}
    }
    // Sequential progression: a learner can enter only the highest level unlocked by completing the previous one.
    private int unlockedLevel(int module){
        if(isPremium()&&module>=1&&module<=6)return 100;
        String key="module"+module+"_unlocked";
        int saved=prefs.getInt(key,module==1?1:1);
        return Math.max(1,Math.min(100,saved));
    }
    private boolean canEnterLevel(int module,int requested){
        return requested>=1 && requested<=unlockedLevel(module);
    }

    private void showLearn(){base("Learning Journey",1);heading("Choose your adventure");String[] modules={"A1 — First Steps","A1 — Everyday Adventures","B1 — Confident English","B2 — Vocabulary Quest","C1 — Real-Life Word Quest","C2 — Advanced Word Studio","Travel English","Business English","Academic English","Culture & Conversation"};String[] icons={"🌱","🧭","🌳","🚀","🎓","🏆","✈️","💼","📚","🌍"};int m1=isPremium()?100:Math.min(100,prefs.getInt("module1_unlocked",1));int m2=isPremium()?100:Math.min(100,prefs.getInt("module2_unlocked",0));int m3=isPremium()?100:Math.min(100,prefs.getInt("module3_unlocked",0));int m4=isPremium()?100:Math.min(100,prefs.getInt("module4_unlocked",0));int m5=isPremium()?100:Math.min(100,prefs.getInt("module5_unlocked",0));int m6=isPremium()?100:Math.min(100,prefs.getInt("module6_unlocked",0));for(int i=0;i<10;i++){final int m=i;String state=i==0?"   •  "+m1+"/100 LEVELS":i==1?(m2>0?"   •  "+m2+"/100 LEVELS":"   •  COMPLETE MODULE 1 TO UNLOCK"):i==2?(m3>0?"   •  "+m3+"/100 LEVELS":"   •  COMPLETE MODULE 2 TO UNLOCK"):i==3?(m4>0?"   •  "+m4+"/100 LEVELS":"   •  COMPLETE MODULE 3 TO UNLOCK"):i==4?(m5>0?"   •  "+m5+"/100 LEVELS":"   •  COMPLETE MODULE 4 TO UNLOCK"):i==5?(m6>0?"   •  "+m6+"/100 LEVELS":"   •  COMPLETE MODULE 5 TO UNLOCK"):"   •  COMING NEXT";TextView t=txt(icons[i]+"   MODULE "+(i+1)+"\n"+modules[i]+state,17,WHITE,true);card(t,i==0?0xFF159A68:i==1&&m2>0?0xFF167BB5:i==2&&m3>0?0xFF9A55C8:0xFF44577E);t.setOnClickListener(v->{if(m==0)showModuleOne();else if(m==1){if(m2>0)showModuleTwo();else message("Complete all 100 levels of Module 1 to unlock Module 2!");}else if(m==2){if(m3>0)showModuleThree();else message("Complete all 100 levels of Module 2 to unlock Module 3!");}else if(m==3){if(m4>0)showModuleFour();else message("Complete all 100 levels of Module 3 to unlock Module 4!");}else if(m==4){if(m5>0)showModuleFive();else message("Complete all 100 levels of Module 4 to unlock Module 5!");}else if(m==5){if(m6>0)showModuleSix();else message("Complete all 100 levels of Module 5 to unlock Module 6!");}else message("This adventure is coming soon. Keep learning!");});}}
    private void showModuleOne(){base("Module 1 — First Steps",1);TextView intro=txt("🐦  FIRST STEPS\nLearn English one question at a time. Complete all 100 levels!",17,WHITE,true);intro.setGravity(Gravity.CENTER);card(intro,0xFF1264B5);int unlocked=unlockedLevel(1);for(int start=1;start<=100;start+=10){heading("UNIT "+(((start-1)/10)+1)+"  •  "+new String[]{"Greetings","People","Food","Home","School","Daily Life","Places","Travel","Review","Final Quest"}[(start-1)/10]);TextView art=txt(new String[]{"👋 🧑‍🤝‍🧑","👨‍👩‍👧‍👦 🏡","🍎 🍔 🥗","🏠 🛋️ 🪴","🎒 📚 ✏️","⏰ 🏃‍♂️ ☀️","🏙️ 🏪 🚏","✈️ 🧳 🌍","🧩 ⭐ 🏅","🏆 🦉 🎓"}[(start-1)/10],36,WHITE,true);art.setGravity(Gravity.CENTER);card(art,0xFF167B91);LinearLayout grid=row();for(int n=start;n<start+10;n++){final int target=n;TextView cell=txt((n<=unlocked?"●":"🔒")+"\n"+n,14,n<=unlocked?WHITE:0xFFB9C7E8,true);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(4),dp(10),dp(4),dp(10));cell.setBackground(gradient(n<=unlocked?0xFF16A66A:0xFF46577E,0xFF142D70,14));cell.setElevation(dp(8));cell.setTranslationZ(dp(2));((GradientDrawable)cell.getBackground()).setStroke(dp(2),0x88FFFFFF);LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(0,dp(58),1);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(cell,gp);cell.setOnClickListener(v->{if(target<=unlocked){moduleLevel=target;moduleOneFeedback=0;moduleOneSelected=-1;showModuleLesson();}else message("🔒 Locked! Pass Level "+(target-1)+" to unlock Level "+target+".");});if(n%5==0&&n<start+9){content.addView(grid);grid=row();}}content.addView(grid);} }
    private void addXp(int amount){xp+=amount*(isPremium()?2:1);}
    private void addGems(int amount){gems+=amount*(isPremium()?2:1);}
    private boolean isPremium(){return prefs!=null && prefs.getBoolean("premium_active",false);}
    private void consumeHeart(){if(isPremium()){hearts=5;saveState();return;}hearts=Math.max(0,hearts-1);if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();saveState();}
    private void activatePremiumDemo(boolean enabled){prefs.edit().putBoolean("premium_active",enabled).apply();if(enabled){hearts=5;prefs.edit().putInt("hearts",5).putLong("heart_timer_anchor",0L).apply();}showPremium();}
    private static final long HEART_REGEN_MS = 30L * 60L * 1000L;
    private void refreshHearts(){
        if(isPremium()){if(hearts!=5){hearts=5;prefs.edit().putInt("hearts",5).putLong("heart_timer_anchor",0L).apply();}return;}
        long now=System.currentTimeMillis();
        long anchor=prefs.getLong("heart_timer_anchor",0L);
        if(hearts<5){
            if(anchor<=0L){anchor=now;prefs.edit().putLong("heart_timer_anchor",anchor).apply();}
            long elapsed=now-anchor;
            int restored=(int)(elapsed/HEART_REGEN_MS);
            if(restored>0){hearts=Math.min(5,hearts+restored);anchor=hearts>=5?0L:anchor+(restored*HEART_REGEN_MS);prefs.edit().putInt("hearts",hearts).putLong("heart_timer_anchor",anchor).apply();}
        }
    }
    private String heartWaitText(){
        refreshHearts(); if(hearts>=5)return "Full energy";
        long anchor=prefs.getLong("heart_timer_anchor",System.currentTimeMillis());
        long remain=HEART_REGEN_MS-(System.currentTimeMillis()-anchor);
        long mins=Math.max(0,(remain+59999)/60000);
        return "Next heart in "+mins+" min";
    }
    private void showHeartRefill(){
        refreshHearts();base("Energy is empty",1);
        OwlMascotView bird=new OwlMascotView();card(bird,0xFF159BCB);
        TextView talk=txt("Энергия закончилась. Отдохни немного — одно сердечко восстановится через 30 минут. Я буду ждать тебя!",18,WHITE,true);talk.setGravity(Gravity.CENTER);card(talk,0xFF1455B5);
        TextView meter=txt("❤️  "+hearts+" / 5\n\n"+heartWaitText(),20,GOLD,true);meter.setGravity(Gravity.CENTER);card(meter,0xFF123C91);
        addButton("Back to levels",this::showModuleOne);
    }
    private void showModuleLesson(){
        if(!canEnterLevel(1,moduleLevel)){
            moduleLevel=unlockedLevel(1);
            message("Level locked! Complete the previous level to unlock this one.");
            showModuleOne();
            return;
        }
        refreshHearts();
        if(!isPremium()&&hearts<=0){showHeartRefill();return;}
        final int unit=(moduleLevel-1)/10;
        final int step=Math.max(0,Math.min(9,prefs.getInt("m1_step_"+moduleLevel,0)));
        moduleOneStep=step;
        speakQuestionOnce("m1-"+moduleLevel+"-"+step,m1Sentences[unit][step]+". Choose the best answer.");
        base("MODULE 1  •  FIRST STEPS",1);

        LinearLayout hud=row();
        TextView back=txt("‹",34,WHITE,true);back.setGravity(Gravity.CENTER);
        hud.addView(back,new LinearLayout.LayoutParams(dp(42),dp(44)));
        TextView counter=txt((step+1)+" / 10",20,WHITE,true);counter.setGravity(Gravity.CENTER);
        hud.addView(counter,new LinearLayout.LayoutParams(0,dp(44),1));
        StringBuilder heartIcons=new StringBuilder();for(int h=0;h<hearts;h++)heartIcons.append("❤️ ");
        TextView heart=txt(heartIcons.toString(),17,WHITE,true);heart.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        hud.addView(heart,new LinearLayout.LayoutParams(-2,dp(44)));
        content.addView(hud);back.setOnClickListener(v->showModuleOne());

        ProgressBar progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(10);progress.setProgress(step);
        LinearLayout.LayoutParams barP=new LinearLayout.LayoutParams(-1,dp(8));barP.bottomMargin=dp(12);content.addView(progress,barP);

        LinearLayout questionCard=col();questionCard.setGravity(Gravity.CENTER);
        questionCard.setPadding(dp(16),dp(16),dp(16),dp(16));
        questionCard.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,26));questionCard.setElevation(dp(7));
        String[] pictures={"👋","👨‍👩‍👧‍👦","🍎","🏠","🎒","⏰","🏙️","✈️","🧩","🏆"};
        TextView picture=txt(pictures[unit],76,0xFF12316F,true);picture.setGravity(Gravity.CENTER);
        questionCard.addView(picture,new LinearLayout.LayoutParams(-1,dp(112)));
        TextView sentence=txt(m1Sentences[unit][step],22,0xFF10265B,true);
        sentence.setGravity(Gravity.CENTER);sentence.setPadding(dp(8),dp(8),dp(8),dp(8));
        questionCard.addView(sentence,new LinearLayout.LayoutParams(-1,-2));
        TextView prompt=txt("What is this sentence used for?",18,0xFF10265B,true);
        prompt.setGravity(Gravity.CENTER);prompt.setPadding(0,dp(8),0,dp(4));questionCard.addView(prompt);
        LinearLayout.LayoutParams qP=new LinearLayout.LayoutParams(-1,-2);qP.bottomMargin=dp(12);content.addView(questionCard,qP);

        LinearLayout tutor=row();tutor.setGravity(Gravity.CENTER_VERTICAL);
        tutor.setPadding(dp(10),dp(7),dp(12),dp(7));tutor.setBackground(gradient(0xFF0C55C8,0xFF07317F,22));
        OwlMascotView owl=new OwlMascotView();tutor.addView(owl,new LinearLayout.LayoutParams(dp(88),dp(88)));
        String tutorText=moduleOneFeedback==1?"Отлично! Правильный ответ! Ты молодец. Продолжим!"
            :moduleOneFeedback==-1?"Не совсем правильно. Попробуй ещё раз — у тебя получится!"
            :"Привет! Прочитай предложение и выбери правильный ответ. Я проверю!";
        TextView speech=txt(tutorText,15,WHITE,true);speech.setPadding(dp(8),dp(4),dp(4),dp(4));
        tutor.addView(speech,new LinearLayout.LayoutParams(0,-2,1));
        LinearLayout.LayoutParams tutorP=new LinearLayout.LayoutParams(-1,-2);tutorP.bottomMargin=dp(12);content.addView(tutor,tutorP);

        TextView section=txt("CHOOSE THE BEST ANSWER",13,0xFFFFD43B,true);
        section.setGravity(Gravity.CENTER);content.addView(section);
        for(int i=0;i<4;i++){
            final int choice=i;
            boolean selected=(moduleOneFeedback!=0 && moduleOneSelected==i);
            int bg=selected?(moduleOneFeedback==1?0xFF22C55E:0xFFE34B5B):0xFFFFFFFF;
            TextView option=txt((char)('A'+i)+"    "+m1Options[unit][i],16,selected?WHITE:0xFF10265B,true);
            option.setGravity(Gravity.CENTER);option.setPadding(dp(12),dp(15),dp(12),dp(15));
            option.setBackground(gradient(bg,selected?bg:0xFFE4F2FF,22));option.setElevation(dp(3));
            LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(-1,-2);op.topMargin=dp(7);content.addView(option,op);
            option.setOnClickListener(v->{
                if(moduleOneFeedback!=0)return;
                if(!isPremium()&&hearts<=0){showHeartRefill();return;}
                moduleOneSelected=choice;
                speakRussian(choice==0?"Правильно! Отличная работа! Продолжаем.":"Пока неверно. Не сдавайся! Посмотри на подсказку и попробуй ещё раз.");
                if(choice==0){moduleOneFeedback=1;addXp(5);addGems(1);dailyDone++;saveState();}
                else{
                    moduleOneFeedback=-1;consumeHeart();
                    if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)
                        prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();
                    saveState();
                }
                showModuleLesson();
            });
        }
        if(moduleOneFeedback==1){
            TextView result=txt("✓  CORRECT!                         +5 XP   💎 +1",17,WHITE,true);
            result.setGravity(Gravity.CENTER);result.setPadding(dp(12),dp(16),dp(12),dp(16));
            result.setBackground(gradient(0xFF20C95A,0xFF079B45,20));
            LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.topMargin=dp(12);content.addView(result,rp);
            addButton(step<9?"CONTINUE  →":"COMPLETE LEVEL  🏆",()->{
                int next=step+1;moduleOneFeedback=0;moduleOneSelected=-1;
                if(next<10){prefs.edit().putInt("m1_step_"+moduleLevel,next).apply();showModuleLesson();}
                else{
                    prefs.edit().putInt("m1_step_"+moduleLevel,0)
                        .putInt("module1_unlocked",Math.max(unlockedLevel(1),Math.min(100,moduleLevel+1))).apply();
                    addXp(15);addGems(3);saveState();
                    new AlertDialog.Builder(this).setTitle("🐦 Level completed!")
                        .setMessage("Все 10 вопросов выполнены!\n+15 XP  •  +3 Gems")
                        .setPositiveButton(moduleLevel<100?"NEXT LEVEL":"FINISH MODULE",(d,w)->{
                            if(moduleLevel<100){moduleLevel++;showModuleLesson();}
                            else{prefs.edit().putInt("module2_unlocked",Math.max(1,prefs.getInt("module2_unlocked",0))).apply();message("Amazing! Module 2 is unlocked!");showLearn();}
                        }).show();
                }
            });
        }else if(moduleOneFeedback==-1){
            TextView result=txt("✕  NOT QUITE — TRY AGAIN",16,WHITE,true);
            result.setGravity(Gravity.CENTER);result.setPadding(dp(12),dp(15),dp(12),dp(15));
            result.setBackground(gradient(0xFFE34B5B,0xFFB91C35,20));
            LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.topMargin=dp(12);content.addView(result,rp);
            TextView hint=txt("Подсказка: "+m1Meanings[unit][step]+"\n❤️  "+hearts+" / 5",14,WHITE,false);
            hint.setGravity(Gravity.CENTER);card(hint,0xFF164A9B);
            addButton(hearts>0?"TRY AGAIN":"RESTORE HEARTS",()->{
                moduleOneFeedback=0;moduleOneSelected=-1;
                if(!isPremium()&&hearts<=0)showHeartRefill();else showModuleLesson();
            });
        }else{
            TextView footer=txt("100 LEVELS  •  10 QUESTIONS EACH  •  EARN XP & GEMS",11,0xFFDCE8FF,false);
            footer.setGravity(Gravity.CENTER);footer.setPadding(0,dp(12),0,dp(8));content.addView(footer);
        }
    }

    private void showModuleTwo(){base("Module 2 — Everyday Adventures",1);TextView intro=txt("🧭  EVERYDAY ADVENTURES\nLearn, listen and answer your way through 100 game levels!",17,WHITE,true);intro.setGravity(Gravity.CENTER);card(intro,0xFF1264B5);int unlocked=unlockedLevel(2);String[] units={"My Daily Routine","At the Café","Around Town","Shopping Time","Friends & Feelings","Weather & Seasons","My Busy Week","Fun and Hobbies","Big Review","Adventure Finale"};String[] art={"⏰ ☀️ 🪥","☕ 🥐 🍰","🏙️ 🚦 🗺️","🛍️ 👟 🎁","🧑‍🤝‍🧑 😊 💬","🌦️ ❄️ 🌈","📅 🚌 🏫","⚽ 🎨 🎵","🧩 ⭐ 🏅","🧭 🏆 🦉"};for(int start=1;start<=100;start+=10){heading("WORLD "+(((start-1)/10)+1)+"  •  "+units[(start-1)/10]);TextView picture=txt(art[(start-1)/10],34,WHITE,true);picture.setGravity(Gravity.CENTER);card(picture,0xFF167B91);LinearLayout grid=row();for(int n=start;n<start+10;n++){final int target=n;TextView cell=txt((n<=unlocked?"🎮":"🔒")+"\n"+n,13,n<=unlocked?WHITE:0xFFB9C7E8,true);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(3),dp(9),dp(3),dp(9));cell.setBackground(gradient(n<=unlocked?0xFF168DCE:0xFF46577E,0xFF142D70,14));cell.setElevation(dp(8));cell.setTranslationZ(dp(2));((GradientDrawable)cell.getBackground()).setStroke(dp(2),0x88FFFFFF);LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(0,dp(58),1);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(cell,gp);cell.setOnClickListener(v->{if(target<=unlocked){moduleLevel=target;showModuleTwoGame();}else message("🔒 Locked! Pass Level "+(target-1)+" to unlock Level "+target+".");});if(n%5==0&&n<start+9){content.addView(grid);grid=row();}}content.addView(grid);}}
    // Module 2: 100 levels × 10 guided sentence lessons. Each level has its own saved step.
    private String moduleTwoSentence(int world,int localLevel,int step){
        String[][] subjects={
          {"I","My sister","We","My parents","Tom","Our family","The children","My friend","My brother","Everyone"},
          {"I","The waiter","We","My friend","She","They","The chef","My parents","He","You"},
          {"I","The bus","Our hotel","My friend","The museum","We","The station","A taxi","You","The visitors"},
          {"I","The shop assistant","We","My sister","This jacket","They","The cashier","My friend","You","The customers"},
          {"I","My best friend","We","She","My classmates","They","My brother","You","Our teacher","Everyone"},
          {"I","The weather","It","We","The sky","My family","The wind","They","Today","Tomorrow"},
          {"I","My team","We","She","The meeting","They","My manager","You","Our class","Everyone"},
          {"I","My friends","We","He","My sister","They","Our team","You","The children","My cousin"},
          {"I","She","We","They","My teacher","He","Our neighbours","You","The students","Everyone"},
          {"I","Our guide","We","The traveller","She","They","My team","You","The explorer","Everyone"}
        };
        String[][] actions={
          {"wake up early","make my bed","prepare breakfast","walk to work","check my schedule","take the bus","finish my tasks","cook dinner","read before bed","go to sleep"},
          {"would like a cup of tea","order a fresh sandwich","ask for the menu","pay the bill","try the local dessert","book a table","recommend a special dish","need a glass of water","enjoy the meal","thank the waiter"},
          {"need to find the station","turn left at the corner","are looking for the museum","can take the number five bus","should cross the street","are waiting near the park","want to visit the old town","can ask for directions","must follow this map","have arrived at the square"},
          {"am looking for a new jacket","would like to try this size","think this price is fair","need a smaller bag","prefer the blue shirt","are comparing two products","can pay by card","want to return this item","found a great offer","are shopping for a gift"},
          {"feel happy today","always listen to my friends","enjoy spending time together","feel worried about the test","help my friends with homework","plan a weekend trip","make everyone laugh","can trust my friends","celebrate small successes","hope to meet again soon"},
          {"think it will rain today","wear a warm coat","love sunny mornings","watch the snow fall","notice cloudy skies this afternoon","usually see storms in spring","feel the cold wind","expect warmer weather tomorrow","take an umbrella with me","enjoy the fresh air"},
          {"have a busy schedule this week","work well with my team","need to prepare a short report","lead the meeting","start at half past nine","am finishing the project","will send the final email","should check the details","learn something new every day","am ready for the next task"},
          {"play football after school","enjoy painting landscapes","practice the guitar every evening","enjoy swimming in summer","go cycling at weekends","watch documentaries with friends","join a dance class","prefer reading adventure stories","love exploring new places","collect interesting photographs"},
          {"have already completed the task","has never visited this place","are learning from our mistakes","have been waiting for an hour","explains the rule clearly","could solve the difficult puzzle","have improved our speaking skills","should review the new vocabulary","are making steady progress","know that practice builds confidence"},
          {"would like to explore the valley","follow the guide through the forest","have discovered an ancient bridge","need to prepare for the journey","am carrying a useful compass","am travelling across the country","will reach the village before sunset","must protect the natural environment","have found a beautiful viewpoint","am proud of this achievement"}
        };
        String[] openers={"Every morning, ","On a typical day, ","After breakfast, ","At the moment, ","Usually, ","This week, ","In the afternoon, ","When I have time, ","Today, ","As part of our plan, "};
        String subject="I";
        String action=actions[world][(step*3+localLevel-1)%10];
        String sentence=openers[(localLevel+step-1)%10]+subject+" "+action+".";
        // Capitalize the first character after the opener while preserving natural sentence casing.
        return sentence.substring(0,1).toUpperCase()+sentence.substring(1);
    }
    private String moduleTwoMeaning(int world,int localLevel,int step){
        String[] notes={
          "Daily routine: use the present simple to describe habits and regular activities.",
          "At a café: use polite requests such as 'would like' when ordering food or drinks.",
          "Around town: use direction words and modal verbs to ask for or give help.",
          "Shopping: use 'would like', 'prefer' and 'how much' to talk about products and prices.",
          "Friends and feelings: use adjectives and common verbs to describe emotions and relationships.",
          "Weather: use weather expressions and the present or future tense to describe conditions.",
          "A busy week: use time expressions and work or study vocabulary to describe plans.",
          "Hobbies: use activity verbs and frequency expressions to talk about free time.",
          "Big review: notice present perfect, modal verbs and useful linking expressions.",
          "Adventure: use descriptive language, travel vocabulary and modal verbs in context."
        };
        return notes[world]+" Focus: sentence "+(step+1)+" in level "+moduleLevel+". Read it aloud, notice the verb phrase, then choose the best learning point.";
    }
    private String[] moduleTwoChoices(int world,int step){
        String[][] choices={
          {"Describe a daily habit","Order food politely","Give street directions","Describe a past holiday"},
          {"Ask for food or a drink","Describe the weather","Talk about a school subject","Buy a train ticket"},
          {"Ask for or give directions","Describe a family member","Order a dessert","Talk about a hobby"},
          {"Talk about shopping and prices","Describe a daily routine","Ask about the weather","Introduce a friend"},
          {"Describe feelings and friendship","Give directions in a city","Order a meal","Describe a work schedule"},
          {"Talk about weather conditions","Ask for a shop discount","Describe a classroom","Plan a restaurant visit"},
          {"Describe a busy schedule or task","Talk about a favourite sport","Ask for a hotel room","Describe a storm"},
          {"Talk about hobbies and free time","Describe a shopping problem","Give directions","Order a coffee"},
          {"Review useful grammar and progress","Describe a café menu","Talk about the seasons","Ask for a bus ticket"},
          {"Describe travel and adventure","Talk about daily breakfast","Choose clothes in a shop","Describe a school friend"}
        };
        return choices[world];
    }
    private void showModuleTwoHeartRefill(){
        refreshHearts();base("HEARTS EMPTY",1);
        OwlMascotView bird=new OwlMascotView();card(bird,0xFF159BCB);
        TextView talk=txt("Сердечки закончились. Отдохни немного — следующее появится через 30 минут. Я буду ждать тебя!",17,WHITE,true);talk.setGravity(Gravity.CENTER);card(talk,0xFF1455B5);
        TextView meter=txt("❤️  "+hearts+" / 5\n\n"+heartWaitText(),20,GOLD,true);meter.setGravity(Gravity.CENTER);card(meter,0xFF123C91);
        addButton("BACK TO LEVELS",this::showModuleTwo);
    }
    private void showModuleTwoGame(){
        if(!canEnterLevel(2,moduleLevel)){
            moduleLevel=unlockedLevel(2);
            message("Level locked! Complete the previous level to unlock this one.");
            showModuleTwo();
            return;
        }
        refreshHearts();if(!isPremium()&&hearts<=0){showModuleTwoHeartRefill();return;}
        final int world=(moduleLevel-1)/10, localLevel=(moduleLevel-1)%10+1;
        final int step=Math.max(0,Math.min(9,prefs.getInt("m2_step_"+moduleLevel,0)));
        speakQuestionOnce("m2-"+moduleLevel+"-"+step,moduleTwoSentence((moduleLevel-1)/10,(moduleLevel-1)%10,step)+". Choose the correct answer.");
        String[] art={"⏰","☕","🗺️","🛍️","😊","🌈","📅","⚽","🧩","🏆"};
        String[] gameType={"ROUTINE QUEST","CAFÉ ROLEPLAY","CITY EXPLORER","SHOPPING SMART","FRIENDSHIP STORY","WEATHER DETECTIVE","WEEK PLANNER","HOBBY CLUB","GRAMMAR BOOST","GRAND ADVENTURE"};
        final String sentence=moduleTwoSentence(world,localLevel,step);
        final String meaning=moduleTwoMeaning(world,localLevel,step);
        final String[] choices=moduleTwoChoices(world,step);
        base("MODULE 2  •  EVERYDAY ADVENTURES",1);

        LinearLayout hud=row();
        TextView back=txt("‹",34,WHITE,true);back.setGravity(Gravity.CENTER);hud.addView(back,new LinearLayout.LayoutParams(dp(42),dp(44)));
        TextView counter=txt((step+1)+" / 10",20,WHITE,true);counter.setGravity(Gravity.CENTER);hud.addView(counter,new LinearLayout.LayoutParams(0,dp(44),1));
        StringBuilder heartIcons=new StringBuilder();for(int h=0;h<hearts;h++)heartIcons.append("❤️ ");TextView heart=txt(heartIcons.toString(),17,WHITE,true);heart.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);hud.addView(heart,new LinearLayout.LayoutParams(-2,dp(44)));
        content.addView(hud);back.setOnClickListener(v->showModuleTwo());
        TextView progressLabel=txt("LEVEL "+moduleLevel+" OF 100     •     WORLD "+(world+1)+" / 10",13,GOLD,true);progressLabel.setGravity(Gravity.CENTER);content.addView(progressLabel);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(10);bar.setProgress(step);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,dp(8));bp.setMargins(0,dp(7),0,dp(12));content.addView(bar,bp);

        LinearLayout quizCard=col();quizCard.setGravity(Gravity.CENTER);quizCard.setPadding(dp(16),dp(16),dp(16),dp(18));quizCard.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,26));quizCard.setElevation(dp(7));
        TextView picture=txt(art[world],76,0xFF12316F,true);picture.setGravity(Gravity.CENTER);quizCard.addView(picture,new LinearLayout.LayoutParams(-1,dp(112)));
        TextView title=txt(gameType[world]+"  •  LEVEL "+localLevel,13,0xFF1764B5,true);title.setGravity(Gravity.CENTER);quizCard.addView(title);
        TextView learn=txt("“"+sentence+"”",20,0xFF10265B,true);learn.setGravity(Gravity.CENTER);learn.setPadding(dp(8),dp(10),dp(8),dp(6));quizCard.addView(learn,new LinearLayout.LayoutParams(-1,-2));
        TextView prompt=txt("What is the main topic of this sentence?",19,0xFF10265B,true);prompt.setGravity(Gravity.CENTER);prompt.setPadding(dp(6),dp(12),dp(6),dp(8));quizCard.addView(prompt);
        LinearLayout.LayoutParams qlp=new LinearLayout.LayoutParams(-1,-2);qlp.bottomMargin=dp(12);content.addView(quizCard,qlp);

        LinearLayout tutor=row();tutor.setGravity(Gravity.CENTER_VERTICAL);tutor.setPadding(dp(8),dp(6),dp(12),dp(6));tutor.setBackground(gradient(0xFF0C55C8,0xFF07317F,22));
        OwlMascotView owl=new OwlMascotView();tutor.addView(owl,new LinearLayout.LayoutParams(dp(88),dp(88)));
        String tutorText=moduleTwoFeedback==1?"Отлично! Это правильный ответ. Ты молодец! Продолжаем!":moduleTwoFeedback==-1?"Не совсем верно. Прочитай подсказку и попробуй ещё раз!":"Привет! Прочитай предложение и выбери правильный вариант. Я сразу проверю ответ!";
        TextView speech=txt(tutorText,15,WHITE,true);speech.setPadding(dp(8),dp(4),dp(4),dp(4));tutor.addView(speech,new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.bottomMargin=dp(10);content.addView(tutor,tp);

        TextView section=txt("CHOOSE THE BEST ANSWER",13,GOLD,true);section.setGravity(Gravity.CENTER);content.addView(section);
        for(int i=0;i<4;i++){
            final int choice=i;boolean selected=moduleTwoFeedback!=0&&moduleTwoSelected==i;
            int bg=selected?(moduleTwoFeedback==1?0xFF20C95A:0xFFE34B5B):0xFFFFFFFF;
            String label=(char)('A'+i)+"    "+choices[i]+(selected?(moduleTwoFeedback==1?"     ✓":"     ✕"):"");
            TextView option=txt(label,16,selected?WHITE:0xFF10265B,true);option.setGravity(Gravity.CENTER);option.setPadding(dp(12),dp(15),dp(12),dp(15));option.setBackground(gradient(bg,selected?bg:0xFFE4F2FF,22));option.setElevation(dp(3));LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(-1,-2);op.topMargin=dp(7);content.addView(option,op);
            option.setOnClickListener(v->{
                if(moduleTwoFeedback!=0)return;
                refreshHearts();if(!isPremium()&&hearts<=0){showModuleTwoHeartRefill();return;}
                moduleTwoSelected=choice;
                speakRussian(choice==0?"Правильно! Молодец! Ты справляешься отлично.":"Это не совсем верно. Подумай ещё раз и попробуй снова.");
                if(choice==0){moduleTwoFeedback=1;addXp(5);addGems(1);dailyDone++;saveState();showModuleTwoGame();}
                else{moduleTwoFeedback=-1;consumeHeart();if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();saveState();showModuleTwoGame();}
            });
        }
        if(moduleTwoFeedback==1){
            TextView result=txt("✓  CORRECT!                         +5 XP   💎 +1",17,WHITE,true);result.setGravity(Gravity.CENTER);result.setPadding(dp(12),dp(16),dp(12),dp(16));result.setBackground(gradient(0xFF20C95A,0xFF079B45,20));LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.topMargin=dp(12);content.addView(result,rp);
            TextView note=txt("💡  "+meaning,14,0xFF10265B,false);note.setPadding(dp(12),dp(10),dp(12),dp(10));card(note,0xFFE8F3FF);
            addButton(step<9?"CONTINUE  →":"COMPLETE LEVEL  🏆",()->{
                moduleTwoFeedback=0;moduleTwoSelected=-1;int next=step+1;
                if(next<10){prefs.edit().putInt("m2_step_"+moduleLevel,next).apply();showModuleTwoGame();}
                else{prefs.edit().putInt("m2_step_"+moduleLevel,0).putInt("module2_unlocked",Math.max(unlockedLevel(2),Math.min(100,moduleLevel+1))).apply();addXp(15);addGems(3);saveState();new AlertDialog.Builder(this).setTitle("🐦 LEVEL COMPLETE!").setMessage("Ты ответил на все 10 вопросов! Отличная работа.\n\n+15 XP  •  +3 Gems").setPositiveButton(moduleLevel<100?"NEXT LEVEL":"FINISH MODULE",(d,w)->{if(moduleLevel<100){moduleLevel++;showModuleTwoGame();}else{prefs.edit().putInt("module3_unlocked",Math.max(1,prefs.getInt("module3_unlocked",0))).apply();message("Amazing! You completed all 100 levels of Module 2. Module 3 is unlocked!");showLearn();}}).show();}
            });
        }else if(moduleTwoFeedback==-1){
            TextView result=txt("✕  NOT QUITE — TRY AGAIN",16,WHITE,true);result.setGravity(Gravity.CENTER);result.setPadding(dp(12),dp(15),dp(12),dp(15));result.setBackground(gradient(0xFFE34B5B,0xFFB91C35,20));LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.topMargin=dp(12);content.addView(result,rp);
            TextView hint=txt("Подсказка: "+meaning+"\n❤️  "+hearts+" / 5",14,WHITE,false);hint.setGravity(Gravity.CENTER);card(hint,0xFF164A9B);
            addButton(hearts>0?"TRY AGAIN":"RESTORE HEARTS",()->{moduleTwoFeedback=0;moduleTwoSelected=-1;if(!isPremium()&&hearts<=0)showModuleTwoHeartRefill();else showModuleTwoGame();});
        }else{addButton("🔊  LISTEN TO THE SENTENCE",()->speak(sentence));TextView footer=txt("100 LEVELS  •  10 QUESTIONS EACH  •  EARN XP & GEMS",11,0xFFDCE8FF,false);footer.setGravity(Gravity.CENTER);content.addView(footer);}
    }
    // Module 3: 100 levels x 10 sentence lessons, with picture-led mini-games.
    // The existing 100 illustrated vocabulary items are reused in fresh, level-specific sentences.
    private final String[][] m3Words={
      {"apple|себ|🍎","banana|банан|🍌","grape|ангур|🍇","peach|шафтолу|🍑","lemon|лимӯ|🍋","carrot|сабзӣ|🥕","onion|пиёз|🧅","potato|картошка|🥔","cheese|панир|🧀","honey|асал|🍯"},
      {"bedroom|хонаи хоб|🛏️","kitchen|ошхона|🍳","window|тиреза|🪟","curtain|парда|🪟","pillow|болишт|🛌","blanket|кӯрпа|🛏️","mirror|оина|🪞","stairs|зина|🪜","ceiling|шифт|🏠","balcony|айвон|🏡"},
      {"doctor|духтур|🧑‍⚕️","nurse|ҳамшира|👩‍⚕️","teacher|омӯзгор|🧑‍🏫","driver|ронанда|🚕","farmer|деҳқон|👨‍🌾","builder|бинокор|👷","artist|рассом|🎨","chef|ошпаз|👨‍🍳","pilot|халабон|👨‍✈️","dentist|дандонпизишк|🦷"},
      {"airport|фурудгоҳ|✈️","ticket|чипта|🎫","suitcase|ҷомадон|🧳","platform|платформа|🚉","passenger|мусофир|🧍","passport|шиноснома|🛂","journey|сафар|🗺️","luggage|бор|🧳","departure|рафтан|🛫","arrival|расидан|🛬"},
      {"mountain|кӯҳ|⛰️","river|дарё|🏞️","forest|ҷангал|🌲","island|ҷазира|🏝️","desert|биёбон|🏜️","waterfall|шаршара|💦","valley|водӣ|🌄","coast|соҳил|🏖️","lake|кӯл|🌊","cave|ғор|🕳️"},
      {"wallet|ҳамён|👛","receipt|расид|🧾","discount|тахфиф|🏷️","cashier|хазинадор|🧑‍💼","customer|харидор|🛍️","price|нарх|💲","change|бақия|🪙","market|бозор|🏬","basket|сабад|🧺","queue|навбат|🚶"},
      {"sunny|офтобӣ|☀️","cloudy|абрнок|☁️","windy|шамолӣ|🌬️","storm|тӯфон|⛈️","thunder|раъд|🌩️","lightning|барқ|⚡","fog|туман|🌫️","frost|шабнам|❄️","rainbow|тирукамон|🌈","drizzle|борони реза|🌦️"},
      {"elbow|оринҷ|💪","shoulder|китф|🧍","thumb|ангушти калон|👍","ankle|буҷулак|🦶","forehead|пешонӣ|🙂","chin|манаҳ|🙂","wrist|банди даст|⌚","knee|зону|🦵","neck|гардан|🧣","cheek|рухсора|😊"},
      {"honest|ростқавл|🤝","patient|сабур|🧘","curious|кунҷков|🧐","brave|ҷасур|🦁","polite|боадаб|🙏","generous|саховатманд|🎁","careful|эҳтиёткор|🛡️","confident|боваринок|😎","creative|эҷодкор|🎨","reliable|боэътимод|✅"},
      {"discover|кашф кардан|🔎","improve|беҳтар кардан|📈","compare|муқоиса кардан|⚖️","protect|ҳифз кардан|🛡️","explain|шарҳ додан|💬","suggest|пешниҳод кардан|💡","prepare|омода кардан|📝","achieve|ба даст овардан|🏆","decide|қарор кардан|🧠","imagine|тасаввур кардан|🌈"}
    };
    private final String[] m3Worlds={"Food Market","Home Explorer","People at Work","Travel Quest","Nature Explorer","Smart Shopping","Weather Watch","Body & Health","Personality Garden","Power Verbs"};
    private final String[] m3Contexts={"at the food market","around the house","at work","while travelling","in the natural world","while shopping","when talking about the weather","when describing the body","when describing people","in everyday conversations"};
    private String[] m3Item(int world,int step,int local){return m3Words[world][(step+local)%10].split("\\|");}
    // Ten varied, topic-aware example sentences per unit prevent the same lesson text repeating.
    private final String[][] m3Frames={
      {"I chose %s at the food market.","We talked about %s at lunch.","The shop has %s today.","I can recognise %s in this picture.","Please look for %s on the menu.","My family likes %s.","We learned the word %s today.","Can you spell %s correctly?","I wrote %s in my notebook.","I can use %s in a sentence."},
      {"I can see the %s in my home.","Please take care of the %s.","The %s makes this room comfortable.","We talked about the %s in class.","I cleaned the %s this morning.","Where is the %s?","The %s is easy to find.","I learned a new word: %s.","Can you describe the %s?","I can use %s when describing a home."},
      {"A %s helps people every day.","My friend wants to become a %s.","The %s works with other people.","We learned about the %s at school.","I asked the %s a question.","A good %s needs useful skills.","The %s has an important job.","Can you name this job: %s?","I read a story about a %s.","I can describe what a %s does."},
      {"I saw the word %s at the airport.","The %s is connected with travelling.","We learned the word %s before our trip.","I wrote %s on my travel list.","Can you explain the word %s?","I heard %s during the journey.","The guide used the word %s.","I can recognise %s in a travel story.","We practised saying %s aloud.","I can use %s when talking about travel."},
      {"We learned about %s in nature class.","I saw the word %s in a nature book.","The picture shows %s.","We talked about %s during our walk.","Can you describe %s?","I wrote %s in my nature notebook.","The lesson introduced the word %s.","I can recognise %s in a picture.","We practised spelling %s.","I can use %s when describing nature."},
      {"We used the word %s in a shopping conversation.","I saw %s in the shop.","The lesson explains the word %s.","Can you use %s in a sentence?","I wrote %s on my shopping list.","We practised saying %s aloud.","I can recognise %s in a shop.","The customer asked about %s.","I learned how to spell %s.","I can use %s when shopping."},
      {"We learned the word %s in our weather lesson.","The forecast mentioned %s.","I heard %s in a weather report.","Can you describe %s?","I wrote %s in my notebook.","The picture helps me remember %s.","We practised saying %s aloud.","I can recognise %s in a forecast.","The teacher explained %s.","I can use %s when talking about weather."},
      {"The word %s names a part of the body.","I learned about %s in health class.","Can you point to the %s?","The picture shows the %s.","I wrote %s in my health notebook.","We practised saying %s aloud.","The doctor asked about my %s.","I can recognise the word %s.","The lesson explained %s.","I can use %s when describing the body."},
      {"She is known for being %s.","Being %s is a valuable quality.","A %s person treats others with respect.","We discussed what %s means.","Can you describe someone who is %s?","I want to be more %s.","The story describes a %s character.","I wrote the word %s in my notebook.","We practised using %s in a sentence.","I can describe a person as %s."},
      {"We can %s this task together.","I want to %s something useful today.","Can you %s the problem?","The teacher asked us to %s the idea.","I learned how to %s more carefully.","We should %s before making a choice.","I can %s my skills with practice.","Try to %s a new solution.","I used the verb %s in my sentence.","I can use %s when explaining an action."}
    };
    private String m3Sentence(String word,int world,int step,int local){
        return String.format(Locale.US,m3Frames[world][(step+local)%10],word);
    }
    private void showModuleThree(){base("Module 3 — Sentence Explorer",1);TextView intro=txt("Explore 100 picture-rich levels. Learn 10 useful new example sentences in every level, listen, play and earn rewards!",17,WHITE,true);intro.setGravity(Gravity.CENTER);card(intro,0xFF7542B8);OwlMascotView moduleOwl=new OwlMascotView();LinearLayout.LayoutParams owlParams=new LinearLayout.LayoutParams(dp(150),dp(140));owlParams.gravity=Gravity.CENTER;content.addView(moduleOwl,owlParams);int unlocked=unlockedLevel(3);for(int start=1;start<=100;start+=10){int world=(start-1)/10;heading("WORLD "+(world+1)+"  •  "+m3Worlds[world]);TextView picture=txt(m3Words[world][0].split("\\|")[2]+"   "+m3Words[world][4].split("\\|")[2]+"   "+m3Words[world][8].split("\\|")[2],38,WHITE,true);picture.setGravity(Gravity.CENTER);card(picture,0xFF7542B8);LinearLayout grid=row();for(int n=start;n<start+10;n++){final int target=n;TextView cell=txt((n<=unlocked?"🧩":"🔒")+"\n"+n,13,n<=unlocked?WHITE:0xFFB9C7E8,true);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(3),dp(9),dp(3),dp(9));cell.setBackground(gradient(n<=unlocked?0xFF9455D5:0xFF46577E,0xFF35216F,14));cell.setElevation(dp(8));cell.setTranslationZ(dp(2));((GradientDrawable)cell.getBackground()).setStroke(dp(2),0x88FFFFFF);LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(0,dp(58),1);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(cell,gp);cell.setOnClickListener(v->{if(target<=unlocked){moduleLevel=target;showModuleThreeGame();}else message("🔒 Locked! Pass Level "+(target-1)+" to unlock Level "+target+".");});if(n%5==0&&n<start+9){content.addView(grid);grid=row();}}content.addView(grid);}}
    private void showModuleThreeHeartRefill(){
        refreshHearts();base("Energy is empty",1);
        OwlMascotView mascot=new OwlMascotView();LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(dp(190),dp(180));mp.gravity=Gravity.CENTER;content.addView(mascot,mp);
        TextView talk=txt("Похоже, сердечки закончились. Сделай небольшой перерыв — одно сердечко восстановится через 30 минут. Я буду ждать тебя!",17,WHITE,true);talk.setGravity(Gravity.CENTER);card(talk,0xFF1455B5);
        TextView meter=txt("❤️  "+hearts+" / 5\n\n"+heartWaitText(),20,GOLD,true);meter.setGravity(Gravity.CENTER);card(meter,0xFF123C91);
        addButton("Back to levels",this::showModuleThree);
    }
    private void showModuleThreeGame(){
        if(!canEnterLevel(3,moduleLevel)){
            moduleLevel=unlockedLevel(3);
            message("Level locked! Complete the previous level to unlock this one.");
            showModuleThree();
            return;
        }
        refreshHearts();if(!isPremium()&&hearts<=0){showModuleThreeHeartRefill();return;}
        int world=(moduleLevel-1)/10, local=(moduleLevel-1)%10;
        int step=Math.max(0,Math.min(9,prefs.getInt("m3_step_"+moduleLevel,0)));
        speakQuestionOnce("m3-"+moduleLevel+"-"+step,"Look at the picture, read the example, and choose the correct meaning.");
        String[] item=m3Item(world,step,local);String word=item[0],meaning=item[1],emoji=item[2];
        String sentence=m3Sentence(word,world,step,local);
        String[] options=new String[4];options[0]=meaning;
        for(int j=1;j<4;j++){String[] distractor=m3Words[world][(step+j+local+1)%10].split("\\|");options[j]=distractor[1];}
        List<String> choices=new ArrayList<>(Arrays.asList(options));Collections.shuffle(choices,new Random(moduleLevel*31L+step));
        base("Module 3  •  Level "+moduleLevel,1);
        LinearLayout stats=row();TextView heart=txt("❤️  "+hearts+" / 5",16,GOLD,true);stats.addView(heart,new LinearLayout.LayoutParams(0,-2,1));stats.addView(txt("⭐  "+xp+" XP",15,WHITE,true));content.addView(stats);
        TextView progress=txt("QUESTION "+(step+1)+" OF 10     •     WORLD "+(world+1)+" / 10",14,GOLD,true);progress.setGravity(Gravity.CENTER);content.addView(progress);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(10);bar.setProgress(step);content.addView(bar,new LinearLayout.LayoutParams(-1,dp(10)));space(content,12);
        OwlMascotView mascot=new OwlMascotView();LinearLayout.LayoutParams mascotParams=new LinearLayout.LayoutParams(dp(172),dp(158));mascotParams.gravity=Gravity.CENTER;content.addView(mascot,mascotParams);
        TextView birdLine=txt("Привет! Давай изучим предложение. Выбери правильный ответ — я сразу проверю его и объясню ошибку.",16,WHITE,true);birdLine.setGravity(Gravity.CENTER);birdLine.setPadding(dp(14),dp(12),dp(14),dp(12));card(birdLine,0xFF1455B5);
        TextView art=txt(emoji,66,0xFF35216F,true);art.setGravity(Gravity.CENTER);art.setBackground(gradient(0xFFFFFFFF,0xFFE9DCFF,25));content.addView(art,new LinearLayout.LayoutParams(-1,dp(125)));space(content,10);
        TextView badge=txt("NEW WORD  •  "+(step+1)+" / 10",13,GOLD,true);badge.setGravity(Gravity.CENTER);content.addView(badge);
        TextView vocab=txt(word.toUpperCase(Locale.US),27,WHITE,true);vocab.setGravity(Gravity.CENTER);card(vocab,0xFF7542B8);
        TextView translation=txt("Meaning: "+meaning,16,0xFFE9E1FF,true);translation.setGravity(Gravity.CENTER);content.addView(translation);
        TextView example=txt("“"+sentence+"”",17,WHITE,true);example.setGravity(Gravity.CENTER);example.setPadding(dp(12),dp(14),dp(12),dp(14));card(example,0xFF3154A0);
        addButton("🔊  Listen to the word",()->speak(word));addButton("🔊  Listen to the example sentence",()->speak(sentence));
        TextView title=txt("PICTURE QUIZ — CHOOSE THE MEANING",16,GOLD,true);title.setGravity(Gravity.CENTER);content.addView(title);
        TextView hint=txt("Look at the picture, read the example, then choose the correct meaning.",14,0xFFDCE8FF,false);hint.setGravity(Gravity.CENTER);content.addView(hint);
        for(int i=0;i<4;i++){
            final String selected=choices.get(i);TextView option=txt(selected,16,0xFF10265B,true);option.setGravity(Gravity.CENTER);option.setPadding(dp(12),dp(15),dp(12),dp(15));option.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,22));option.setElevation(dp(3));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(8);content.addView(option,lp);
            option.setOnClickListener(v->{refreshHearts();if(!isPremium()&&hearts<=0){showModuleThreeHeartRefill();return;}
                if(selected.equals(meaning)){
                    speakRussian("Правильно! Отличная работа! Ты хорошо запомнил это слово.");
                    addXp(7);addGems(1);dailyDone++;saveState();
                    if(step<9){prefs.edit().putInt("m3_step_"+moduleLevel,step+1).apply();new AlertDialog.Builder(this).setTitle("🦉 Отлично! Правильно!").setMessage("Ты выбрал правильный ответ! Молодец.\n\n"+word+" — "+meaning+"\n\n"+sentence+"\n\n+7 XP • +1 Gem").setPositiveButton("NEXT QUESTION",(d,w)->showModuleThreeGame()).show();}
                    else{prefs.edit().putInt("m3_step_"+moduleLevel,0).putInt("module3_unlocked",Math.max(unlockedLevel(3),Math.min(100,moduleLevel+1))).apply();addXp(20);addGems(4);saveState();new AlertDialog.Builder(this).setTitle("🦉 Level complete! 🏆").setMessage("Прекрасная работа! Ты ответил на все 10 вопросов.\n\n+20 XP • +4 Gems\nLevel "+moduleLevel+" completed!").setPositiveButton(moduleLevel<100?"Next level":"Finish Module 3",(d,w)->{if(moduleLevel<100){moduleLevel++;showModuleThreeGame();}else{prefs.edit().putInt("module4_unlocked",Math.max(1,prefs.getInt("module4_unlocked",0))).apply();message("Amazing! You completed all 100 levels of Module 3. Module 4 is unlocked!");showLearn();}}).show();}
                }else{
                    speakRussian("Пока неверно. Не переживай, ошибки помогают учиться. Попробуй ещё раз.");
                    consumeHeart();if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();saveState();
                    if(hearts==0){new AlertDialog.Builder(this).setTitle("🦉 Ничего страшного!").setMessage("Ответ неверный, и сердечки закончились. Отдохни немного: одно сердечко восстановится через 30 минут.\n\nЯ буду ждать тебя!").setPositiveButton("OK",(d,w)->showModuleThreeHeartRefill()).show();}
                    else{new AlertDialog.Builder(this).setTitle("🦉 Почти получилось!").setMessage("Пока это неверный ответ. Не сдавайся!\n\nПодсказка: "+meaning+"\n\nОсталось сердечек: "+hearts+" / 5").setPositiveButton("TRY AGAIN",(d,w)->showModuleThreeGame()).show();}
                }
            });
        }
        TextView footer=txt("10 QUESTIONS PER LEVEL  •  LEARN → LISTEN → PLAY → UNLOCK",13,0xFFDCE8FF,false);footer.setGravity(Gravity.CENTER);content.addView(footer);
    }

    // Module 4: 100 vocabulary levels, ten distinct words per level (1,000 words total).
    private final String[] m4Words = {
        "time face like know went made little long came room",
        "even eyes come thought people life hand began great asked",
        "looked back good part government felt seemed place house against",
        "nothing heard because bone take disease always never three skin",
        "tissue took look last think round found blood power father",
        "both usually small give side form make quite turned door",
        "knew suddenly tell told looking already moment love large chapter",
        "treatment officer voice words hands cases days among everything called",
        "dear congress seen often gave battle history case taken position",
        "however done smile sometimes country free soon understand known soldiers",
        "others become brought along order especially behind women course result",
        "night patient stood joint work anything cause going evidently several",
        "president less passed wife infection matter given feeling world certain",
        "chief front action whether white question movement condition mind possible",
        "alone body morning horse later toward death followed present labor",
        "necessary money almost open until woman nerve want expression things",
        "replied sent troops half south business officers became within mother",
        "england commander year taking wound pain thing number leave america",
        "above added party word parts table home anna find boris",
        "either near tissues constitution enemy continued fact high letter four",
        "project public common held talk example west illustration important national",
        "cried friend carried entered received five land surface light cannot",
        "next used glands different ever fire itself union really twenty",
        "around early saying sitting best full better british evening gutenberg",
        "bones horses name political road since together thousand cold heart",
        "forms speak shouted means kept impossible arms vessels line moved",
        "becomes rose wish conditions system drawing gone rise force king",
        "everyone short times black formed pressure hair clear fellow laws",
        "longer children remained help ready regiment forward hundred results military",
        "north peace rode wounded answered beyond crowd growth tried lost",
        "news anyone orders past point service across interest understood reached",
        "strange close process rather read sound beside frequently happy opinion",
        "self spoke standing coming limb making opened presence trade till",
        "deep formation soldier affairs aneurysm operation show wanted field raised",
        "thus english slavery family repeated stopped rest wished following happened",
        "perhaps turning colonies seeing applied answer true muscles affected events",
        "neck occur period talking kind revolution able else foot appeared",
        "call rapidly associated german husband lower southern abscess terrible york",
        "least lymph returned spread company local middle attention features noticed",
        "reason campaign return wall merely silent steps turn whose effect",
        "water giving laid therefore window wounds federal person soft speaking",
        "subject dinner size believe daughter doctor hear honor strength waiting",
        "colonial immediately placed quickly conversation dark questions trying city hard",
        "street feet measures view doing removed usual account brother civil",
        "fell foreign former lady paper child forces glanced nearly republican",
        "enough fine character closed particularly town afraid considerable court hardly",
        "severe single coat freedom please sides tears joints knee symptoms",
        "cancer france ground human meet nature seat syphilis acute covered",
        "gangrene nation remarked society although artery changes neither rights soul",
        "strong spirit swelling adjutant fear girl grew hours reply thin",
        "tone bring late pale according considered dead except mouth area",
        "faces feel remember smiling clinical fresh need someone village lesions",
        "plan special contrary smiled ulcer attack hour members bridge employed",
        "finally friends listened alexander various membrane pass drew europe growing",
        "occurs takes doubt muscle primary command convention described independence appear",
        "change george glad causes decided hope john nerves powers relations",
        "showed voices wrote difficult ordered russians secondary terms virginia drawn",
        "fixed probably direction fingers killed liable lips minutes opening republicans",
        "says unable complete frightened governor natural struck capital holding sister",
        "smoke staff study beginning blue carriage colonel greater happiness loss",
        "moving keep declared direct firm idea master method remain serious",
        "running consists group heavy increased indeed pleasure relation sarcoma simple",
        "step camp dress further influence marked prepared instead lead property",
        "thinking vessel diseases duty industry leaders matters silence appearance chair",
        "expressed living manner march uncle changed chiefly passing prevent seems",
        "veins besides lines popular slowly story term angry attended leaving",
        "lord produced seven activity observed office victory cells clearly east",
        "effort injury months upper dressing established historians latter poor sight",
        "similar tendon corner eight excellency live loved series silver broken",
        "captain destroyed divided everybody peasants wait expected future book gold",
        "organisms shoulders thoughts ball danger fall straight tariff explain kissed",
        "resulting sure thirty uniform control easy finger portion pressed prisoners",
        "asking chronic majority occurred purpose rule subcutaneous arrived clock economic",
        "filled health produce remembered certainly commerce election individual persons presented",
        "ulcers articular distance exclaimed interests legs lying meeting quiet church",
        "earth follow generals handsome houses importance mamma massachusetts post region",
        "section spite weeks western written affair glass occupied policy politics",
        "seem shoulder vein bacteria below increase laughing leading meaning movements",
        "note ought suffrage truth bright diagnosis drove easily hold papers",
        "reading rushed seized senate slight territory whatever development division lived",
        "marya meant shown sleep treaty advanced farmers guns miss river",
        "suffering tomorrow destruction event royal spent varieties difficulty evident miles",
        "none outside passage railways regarded save settled vote bank citizens",
        "main normal shouting solution appears equal experience injuries letters plain",
        "removal stage circumstances enter hill interrupted involved married surprise care",
        "floor forty healing infected paid value wilson aside talked thank",
        "wide cartilage directly showing support surrounded carry connection efforts ends",
        "farther fibrous figure ladies nations nose object progress sense temperature",
        "wrong caused dressed experienced frenchman knows lesion majesty ordinary real",
        "smolensk instant issue larger laughed mass move sherlock authority connective",
        "discharge external flank fluid galloped gentlemen grown learned listen path",
        "population surrounding walked begun bill european extent gentleman happen parties",
        "send sign stop works absence avoid beautiful bent berg consider",
        "fate glancing industrial mean notice ranks signs begin carolina democracy",
        "knowing sake source whispered active break cavity chance cross desire",
        "escape fight finished imagine marriage mucous northern opposition pleasant rapid",
        "reaction readily started tenderness threw advance bound broke closely colour",
        "completely higher marry multiple pennsylvania play pleased practice receive shot",
        "superficial treated week bonaparte cannon degree fifty gazed police retreat",
        "sounds unless adjacent century circulation democrats despite guards hearing hills"
    };
    private final String[] m4Worlds={"Core Vocabulary 1","Core Vocabulary 2","Everyday Language 1","Everyday Language 2","Reading Essentials 1","Reading Essentials 2","Communication 1","Communication 2","Fluency Builder 1","Fluency Builder 2"};
    private final String[] m4Art={"👨‍👩‍👧‍👦 💬 🤝","🏠 🪑 🛋️","🍲 🥑 🍞","🌳 🐾 🌻","🚆 🧳 🗺️","💼 📚 💡","😊 💜 🌟","🏃‍♀️ 🚴‍♂️ 🧗","🔬 💻 ⚙️","🎭 🎨 🌍"};
    // Each of the 100 curriculum rows contains exactly ten words (1,000 total).
    private final String[] m4WordBank=String.join(" ",m4Words).trim().split("\\s+");
    private String m4Word(int level,int step){return m4Words[level-1].trim().split("\\s+")[step];}
    private String m4Example(String word,int level,int step){
        String[] frames={"I learned the word %s today.","Can you spell %s correctly?","I heard someone use %s in a conversation.","Please write %s in your vocabulary notebook.","The word %s is part of my English practice.","I can recognise %s when I read a story.","Let's practise saying %s aloud.","I want to remember the word %s.","Today I added %s to my word list.","I can use %s when I speak English."};
        return String.format(Locale.US,frames[(level+step)%frames.length],word);
    }
    private void showModuleFour(){
        base("Module 4 — Vocabulary Quest",1);
        TextView intro=txt("🦉\nBuild a stronger vocabulary through 100 progressive levels. Study each word, hear its pronunciation, read a model sentence and check its meaning.",17,WHITE,true);intro.setGravity(Gravity.CENTER);card(intro,0xFF168B82);
        int unlocked=unlockedLevel(4);
        for(int start=1;start<=100;start+=10){int world=(start-1)/10;heading("WORLD "+(world+1)+" • "+m4Worlds[world]);TextView art=txt(m4Art[world],34,WHITE,true);art.setGravity(Gravity.CENTER);card(art,0xFF168B82);LinearLayout grid=row();
            for(int n=start;n<start+10;n++){final int target=n;TextView cell=txt((n<=unlocked?"🔤":"🔒")+"\n"+n,13,n<=unlocked?WHITE:0xFFB9C7E8,true);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(3),dp(9),dp(3),dp(9));cell.setBackground(gradient(n<=unlocked?0xFF19A88D:0xFF46577E,0xFF123E69,14));cell.setElevation(dp(8));cell.setTranslationZ(dp(2));((GradientDrawable)cell.getBackground()).setStroke(dp(2),0x88FFFFFF);LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(0,dp(58),1);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(cell,gp);cell.setOnClickListener(v->{if(target<=unlocked){moduleLevel=target;showModuleFourGame();}else message("🔒 Locked! Pass Level "+(target-1)+" to unlock Level "+target+".");});if(n%5==0&&n<start+9){content.addView(grid);grid=row();}}
            content.addView(grid);
        }
    }
    private void showModuleFourHeartRefill(){
        refreshHearts();base("Energy is empty",1);
        OwlMascotView mascot=new OwlMascotView();LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(dp(190),dp(180));mp.gravity=Gravity.CENTER;content.addView(mascot,mp);
        TextView talk=txt("Сердечки закончились. Отдохни немного — одно восстановится через 30 минут. Я буду ждать тебя!",17,WHITE,true);talk.setGravity(Gravity.CENTER);card(talk,0xFF1455B5);
        TextView meter=txt("❤️  "+hearts+" / 5\n\n"+heartWaitText(),20,GOLD,true);meter.setGravity(Gravity.CENTER);card(meter,0xFF123C91);
        addButton("Back to levels",this::showModuleFour);
    }
    private void showModuleFourGame(){
        if(!canEnterLevel(4,moduleLevel)){
            moduleLevel=unlockedLevel(4);
            message("Level locked! Complete the previous level to unlock this one.");
            showModuleFour();
            return;
        }
        refreshHearts();if(!isPremium()&&hearts<=0){showModuleFourHeartRefill();return;}
        int step=Math.max(0,Math.min(9,prefs.getInt("m4_step_"+moduleLevel,0)));
        speakQuestionOnce("m4-"+moduleLevel+"-"+step,"Listen to the word and choose the correct answer.");
        String word=m4Word(moduleLevel,step);String sentence=m4Example(word,moduleLevel,step);String[] emoji={"🧑‍🤝‍🧑","🏡","🍓","🌿","🚀","📘","💖","🏃","🔬","🎨"};
        List<String> options=new ArrayList<>();options.add(word);int baseIndex=(moduleLevel-1)*10+step;
        for(int j=1;j<4;j++){String candidate=m4WordBank[(baseIndex+j*37+13)%m4WordBank.length];int attempts=0;while(options.contains(candidate)&&attempts++<m4WordBank.length){candidate=m4WordBank[(baseIndex+j*37+29+attempts)%m4WordBank.length];}options.add(candidate);}
        Collections.shuffle(options,new Random(moduleLevel*101L+step*17L));
        base("Module 4 • Level "+moduleLevel,1);
        LinearLayout stats=row();TextView heart=txt("❤️  "+hearts+" / 5",16,GOLD,true);stats.addView(heart,new LinearLayout.LayoutParams(0,-2,1));stats.addView(txt("⭐  "+xp+" XP",15,WHITE,true));content.addView(stats);
        TextView progress=txt("QUESTION "+(step+1)+" OF 10   •   VOCABULARY JOURNEY",14,GOLD,true);progress.setGravity(Gravity.CENTER);content.addView(progress);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(10);bar.setProgress(step);content.addView(bar,new LinearLayout.LayoutParams(-1,dp(10)));space(content,12);
        OwlMascotView mascot=new OwlMascotView();LinearLayout.LayoutParams mascotParams=new LinearLayout.LayoutParams(dp(172),dp(158));mascotParams.gravity=Gravity.CENTER;content.addView(mascot,mascotParams);
        TextView birdLine=txt("Привет! Я твой учитель. Давай выучим новое слово! Выбери правильный ответ, а я сразу скажу, верно ли ты ответил.",16,WHITE,true);birdLine.setGravity(Gravity.CENTER);birdLine.setPadding(dp(14),dp(12),dp(14),dp(12));card(birdLine,0xFF1455B5);
        TextView art=txt(emoji[(moduleLevel-1)/10%emoji.length],68,WHITE,true);art.setGravity(Gravity.CENTER);art.setBackground(gradient(0xFFFFFFFF,0xFFD9F8F0,24));content.addView(art,new LinearLayout.LayoutParams(-1,dp(130)));
        TextView wordCard=txt(word.toUpperCase(Locale.US),30,0xFF10265B,true);wordCard.setGravity(Gravity.CENTER);wordCard.setPadding(dp(14),dp(18),dp(14),dp(18));wordCard.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,24));wordCard.setElevation(dp(5));content.addView(wordCard);
        TextView meaning=txt("MEANING\nLooking up a reliable dictionary definition…",16,WHITE,false);meaning.setGravity(Gravity.CENTER);card(meaning,0xFF1765A0);lookupDictionaryMeaning(word,meaning);
        TextView example=txt("Example: “"+sentence+"”",16,WHITE,false);example.setGravity(Gravity.CENTER);card(example,0xFF1765A0);
        addButton("🔊 Listen to the word",()->speak(word));addButton("🔊 Listen to the example",()->speak(sentence));
        TextView prompt=txt("WORD CHALLENGE\nChoose the exact word you just learned.",16,GOLD,true);prompt.setGravity(Gravity.CENTER);content.addView(prompt);
        for(String option:options){final String picked=option;TextView b=txt(option,18,0xFF10265B,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(12),dp(15),dp(12),dp(15));b.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,22));b.setElevation(dp(3));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(8);content.addView(b,lp);b.setOnClickListener(v->{refreshHearts();if(!isPremium()&&hearts<=0){showModuleFourHeartRefill();return;}
            if(picked.equals(word)){
                    speakRussian("Правильно! Отличная работа! Ты выбрал верный ответ.");
                if(step<9){prefs.edit().putInt("m4_step_"+moduleLevel,step+1).apply();addXp(7);addGems(1);dailyDone++;saveState();new AlertDialog.Builder(this).setTitle("🦉 Отлично! Правильно!").setMessage("Верно! Ты выбрал правильное слово. Молодец!\n\n"+word+"\n\n"+sentence+"\n\n+7 XP • +1 Gem").setPositiveButton("NEXT QUESTION",(d,w)->showModuleFourGame()).show();}
                else{prefs.edit().putInt("m4_step_"+moduleLevel,0).putInt("module4_unlocked",Math.max(unlockedLevel(4),Math.min(100,moduleLevel+1))).apply();addXp(20);addGems(4);dailyDone++;saveState();new AlertDialog.Builder(this).setTitle("🦉 LEVEL COMPLETE! 🏆").setMessage("Прекрасная работа! Ты выучил 10 новых слов.\n\n+20 XP • +4 Gems").setPositiveButton(moduleLevel<100?"NEXT LEVEL":"FINISH MODULE",(d,w)->{if(moduleLevel<100){moduleLevel++;showModuleFourGame();}else{prefs.edit().putInt("module5_unlocked",Math.max(1,prefs.getInt("module5_unlocked",0))).apply();message("Amazing! You completed all 100 levels of Module 4. Module 5 is unlocked!");showLearn();}}).show();}
            }else{
                speakRussian("Пока неверно. Не переживай, ошибки помогают учиться. Попробуй ещё раз.");
                consumeHeart();if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();saveState();
                if(hearts==0){new AlertDialog.Builder(this).setTitle("🦉 Ничего страшного!").setMessage("Ответ неверный. Сердечки закончились. Одно сердечко восстановится через 30 минут. Сделай перерыв — я буду ждать тебя!").setPositiveButton("OK",(d,w)->showModuleFourHeartRefill()).show();}
                else{new AlertDialog.Builder(this).setTitle("🦉 Почти получилось!").setMessage("Пока неверно, но не сдавайся!\n\nПравильное слово: "+word+"\nОсталось сердечек: "+hearts+" / 5").setPositiveButton("TRY AGAIN",(d,w)->showModuleFourGame()).show();}
            }
        });}
        TextView footer=txt("10 QUESTIONS PER LEVEL  •  LEARN → LISTEN → PLAY → UNLOCK",13,0xFFDCE8FF,false);footer.setGravity(Gravity.CENTER);content.addView(footer);
    }

    // Module 5: 100 levels, ten vocabulary-learning rounds per level.
    // Uses a dedicated themed word bank and local progress keys, independent of Modules 1–4.
    private final String[] m5Worlds={"Practical Vocabulary 1","Practical Vocabulary 2","Useful Expressions 1","Useful Expressions 2","Work & Study Words","People & Society","Ideas & Opinions","Description & Detail","Reading Challenge","Review & Mastery"};
    private final String[] m5Art={"✈️ 🧳 🛫","🏙️ 🚦 🚇","💼 🗂️ 🤝","🥗 🧘 🩺","💻 📱 🤖","🌎 🌱 ♻️","🎨 🎬 🎵","🛒 💳 🏦","📞 ✉️ 💬","💡 🔭 🧠"};
    private final String[] m5Bank=(
        "ability,acquisition,actively,admirable,admiration,akin,alternative,amuse,apartments,ardent,arresting,ascended,attachment,bags,bandages,bayonets,betrayed,bitterly,block,bloody,boom,bottles,boundaries,brick,brownish,button,cardiac,cared,careless,carelessly,castle,chains,channels,cherished,choked,citizenship,clad,clutching,commonplace,communicated,complain,concessions,conclusions,confederates,confer,confession,conspiracy,contaminated,controversies,convincing,copying,corrected,cough,cream,dashing,decade,decorations,deduce,deeds,deemed,demarcation,determining,develops,differences,diminishing,dine,director,dirt,disgust,disorders,dissemination,disturbances,divide,doctrines,donate,downcast,drowned,enabled,endowed,engrossed," +
        "enlarge,ensure,enumerate,envy,erected,erie,everyday,exceeded,exclude,experiments,export,exposing,fancied,fancies,fiske,fits,flood,flour,flowers,fortified,fourths,francisco,fruits,fund,gaining,glowing,gospel,govern,governess,grasping,groans,guilt,hairy,handful,handing,handling,hereditary,hinder,homesteads,hood,humiliating,impossibility,inability,incidents,indirectly,instinctively,interpreter,irrepressible,irritating,ivory,japanese,jerked,keenly,klapp,knit,lacking,landlord,lantern,latent,lawn,livid,locality,lumber,metivier,ministry,monarchs,morose,mortal,motives,natalie,neglect,neighbouring,nodular,nomination,nourished,obedience,obliteration,ocean,offers,oppose," +
        "ordinance,organizations,outbreak,outburst,owed,pamphlets,participation,patch,patted,peculiarly,penn,perceive,perceptible,perfection,persuade,persuaded,philippine,phrases,piercing,pigmentation,pillows,pitied,pneumonia,polk,potassium,powerless,precautions,precision,prey,priests,privilege,prompt,proofs,puzzled,quit,ragged,rapture,regain,relaxed,remorse,rendering,renew,rescue,resolve,respectable,restriction,rheumatic,rickety,rigor,risks,ross,routes,ruled,sabers,salts,santa,scream,screen,sentiments,separating,sequel,seventeenth,shake,shattered,shifting,shrewd,shrieked,simplest,simulate,socialists,soften,softening,songs,species,specimen,splashing,startling,strained,stretchers,stumbled," +
        "substitute,suppress,supra,suspended,swung,tact,tavern,teachers,theirs,thorough,toil,transmit,treachery,twist,unhealthy,unique,unnaturally,unnoticed,unpleasantly,vehicle,ventilator,villain,violently,virtues,wandering,whisky,whites,withdrew,witty,writings,wyoming,abashed,accidents,accumulated,admired,affectation,alert,alice,ambition,amusement,appealed,approximately,arch,assault,assist,associate,astounding,attendant,aureus,bargain,beds,beforehand,berlin,bitten,blamed,boone,bounds,breathless,bundle,carpets,cathedral,caustic,ceases,centered,certainty,chalk,cheerfully,clark,cleanliness,climbed,clutched,code,coffin,coinage,coldness,colorado,conflicts,constituting,consulting,contented," +
        "continuously,contracts,contradict,contributions,conversing,convey,counties,cracked,crimson,critics,cultivation,curled,dancer,deceive,dedicated,define,democrat,devoid,diet,directing,discontent,discretion,discussing,dish,dismissed,disordered,dispose,disputed,dmitri,doubtless,drawer,earn,email,encapsulated,engine,estimate,exalted,exclusion,expanded,expressly,fantastic,farewell,fascinating,fashionable,flattering,fleeing,floating,fluctuating,frock,funding,gateway,gloomily,grapeshot,gratefully,grieved,grim,grip,guidance,gulf,gunpowder,happier,hartford,harvest,hernia,heroism,hint,holiday,horner,humble,hunters,husbands,ideals,improving,incessant,indirect,inducing,infrequently,injurious,injustice,insensitive," +
        "insurrection,intent,intoxication,introducing,invented,jeffersonian,jewel,jugular,jumping,keys,labored,lacked,lakes,lastly,lawyers,library,lightning,links,lint,liquefaction,liquor,loans,locking,lonely,lords,lowell,magnitude,manly,maturity,melted,menacing,metastases,minnesota,minority,miserable,mixture,moaning,model,modest,momentary,moran,naples,network,nevada,nourishment,nursing,objected,obscure,observations,observer,obstinate,obstruction,organic,oriental,ounce,output,outset,overlooked,panting,payments,perception,perspiration,phagocytes,phenomenon,pierce,pioneer,plains,pleasures,positively,posterity,potatoes,praised,precise,presently,presume,protecting,purified,pursue,rattled,readable," +
        "reader,receipt,refined,reforms,refuge,rejoined,relatively,repulsed,requires,retirement,rigidity,royalty,sacrificed,sailing,salary,salvation,scratch,screams,searched,sensible,sequence,session,shafts,shrill,shrugging,sickness,silly,sink,slim,smeared,snake,sobbed,somehow,speeches,staggered,staircase,stimulating,stones,streamed,streptococcus,stretcher,strips,strongest,submarine,suggesting,swallow,swing,systems,thirst,tipsy,tolstoy,tools,tortuous,tossed,townshend,toxin,traced,tradition,transition,traveled,troyka,tubes,tunica,undecided,undisturbed,uneven,unfamiliar,units,unworthy,urgent,vacancies,vaccines,vigor,waged,ward,warmth,warranties,wednesday,whiskers,whistled," +
        "wills,woodrow,worldly,accepting,alaska,amnesty,annette,antecedent,anyhow,apologize,apology,appetite,appreciate,apron,arsenic,asserted,assuming,attraction,baron,bast,beings,belt,betraying,biographical,bitch,bliss,blown,bradstreet,breadth,brunn,buffalo,buren,bustle,callous,cancers,capitalists,captains,carbon,cautiously,cavernous,cell,centuries,challenged,chart,charters,chisel,civilians,clatter,clearness,coincidence,collapsed,collision,comfortably,comforting,composition,computer,conducting,conferences,confessed,confine,confirmation,confronted,conquered,consistent,conspicuous,contributed,cordial,coughed,courtesy,crash,cumberland,deafening,decades,deceased,deduced,deduction,deity,deliver,demanding,denser," +
        "deposited,depressing,despairing,detailed,detained,developing,devils,devote,diabetes,discoloration,disgrace,disposal,disputing,dissection,dividing,doran,doubled,drained,dread,drives,ecstatic,edmund,elect,eloquent,eminent,empowered,enforced,engineer,enormously,enrolled,entertain,esteem,exerted,exhaustion,expectations,exploit,exploration,farthest,fierce,figs,fills,flush,foreigner,foresight,forge,framed,freshly,frontiers,fuller,funeral,gallant,ganglia,generosity,gleam,gout,grandeur,greenbacks,greetings,grimace,halting,harding,hastening,hayes,heals,hearty,heiress,hints,holders,holland,horns,horrid,horrors,horsemen,humiliation,hungary,hydrogen,hysterical,identical,idleness,ignorant," +
        "immunity,implicate,imply,impress,imprisoned,incomes,indecision,indentured,injunction,insertion,instruction,intending,intolerable,iodide,irritant,jokes,karp,lean,leash,lens,lifeless,limitations,link,linked,listless,lodged,lump,magnificent,malignancy,mask,massive,mechanically,mechanism,midday,molasses,moore,mothers,nasty,natives,negotiate,nodding,obstacle,occupying,oldenburg,orchestra,orel,originally,outlet,outright,overtake,papilloma,passengers,patella,peculiarities,peering,perforated,perplexed,picket,pips,planks,plant,plight,plot,plumes,politely,populists,pregnant,premises,principally,probability,proceeding,proliferation,promoting,prone,puffing,purposely,quincy,recovering,recruits,refrained," +
        "regulating,rejoicing,relapsing,remainder,resection,resemblance,resented,resentment,rhine,richer,rifle,riverside,romance,rome,roots,ruins,ruling,ryder,sacrifices,sarcastic,scarf,scenes,schemes,scientific,secretion,selection,sentinel,shippers,shivering,shooting,shyly,sideways,singly,smallest,sodden,solemnity,soup,spared,speedily,spending,spirited,spots,spotted,springing,sprung,staggering,steep,store,strategy,string,stubborn,student,succumb,superiority,surround,suspect,swear,sweating,systematic,technical,termination,thief,throng,toleration,tour,tout,tradesmen,trails,tramp,tread,trifling,trip,tucked,turkish,unbuttoned,uncovered,undergoing,undressed,unimportant,unusually," +
        "unwilling,uprising,varied,venereal,vent,vermont,vision,visiting,voyage,wanting,weakened,weapons,weariness,whispers,withdrawing,youngest,abolishing,abstract,accessible,accountant,achievement,achievements,acknowledge,affording,afresh,agency,aggression,amazement,amenable,apartment,appeals,appendix,apple,appointments,apportioned,ascending,ascii,ashes,assailed,avert,awakening,background,balcony,barred,beckoned,believing,belly,betray,bible,blade,blew,bloodstained,boat,boats,bride,briony,cash,casting,cents,chimney,civilized,clasped,clearer,coagulation,colleges,collodion,columbia,combat,comforts,commence,commodities,commonwealth,communicating,comparative,complication,conciliation,condemn,congressional,constricting,consultation," +
        "contour,contradictory,convalescence,cool,cordially,correctly,cortex,councils,creaked,creaking,crew,crosses,cultivated,dagger,delicacy,delirious,desk,desperately,desperation,despise,diagnostic,differentiate,differs,dimensions,diminishes,dishes,distracted,documents,doings,doll,dominated,downhill,dowry,dram,dreaded,dred,drums,drying,earthly,electrical"
    ).split(",");
    private String m5Word(int level,int step){return m5Bank[(level-1)*10+step].trim();}
    private void showModuleFive(){
        base("Module 5 — Real-Life Word Quest",1);
        TextView intro=txt("🦉\nPractise vocabulary for everyday situations. Learn the word, hear it, check a dictionary meaning, read an example and complete a knowledge check.",17,WHITE,true);intro.setGravity(Gravity.CENTER);card(intro,0xFFB65B27);
        int unlocked=unlockedLevel(5);
        for(int start=1;start<=100;start+=10){int world=(start-1)/10;heading("WORLD "+(world+1)+" • "+m5Worlds[world]);TextView art=txt(m5Art[world],34,WHITE,true);art.setGravity(Gravity.CENTER);card(art,0xFFB65B27);LinearLayout grid=row();
            for(int n=start;n<start+10;n++){final int target=n;TextView cell=txt((n<=unlocked?"🌟":"🔒")+"\n"+n,13,n<=unlocked?WHITE:0xFFB9C7E8,true);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(3),dp(9),dp(3),dp(9));cell.setBackground(gradient(n<=unlocked?0xFFE58A3B:0xFF46577E,0xFF713B35,14));cell.setElevation(dp(8));cell.setTranslationZ(dp(2));((GradientDrawable)cell.getBackground()).setStroke(dp(2),0x88FFFFFF);LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(0,dp(58),1);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(cell,gp);cell.setOnClickListener(v->{if(target<=unlocked){moduleLevel=target;showModuleFiveGame();}else message("🔒 Locked! Pass Level "+(target-1)+" to unlock Level "+target+".");});if(n%5==0&&n<start+9){content.addView(grid);grid=row();}}content.addView(grid);
        }
    }
    private void showModuleFiveHeartRefill(){
        refreshHearts();base("Energy is empty",1);
        OwlMascotView mascot=new OwlMascotView();LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(dp(205),dp(190));mp.gravity=Gravity.CENTER;content.addView(mascot,mp);
        TextView talk=txt("Сердечки закончились. Сделай небольшой перерыв — следующее сердечко появится через 30 минут. Я буду ждать тебя!",17,WHITE,true);talk.setGravity(Gravity.CENTER);card(talk,0xFF1455B5);
        TextView meter=txt("❤️  "+hearts+" / 5\n\n"+heartWaitText(),20,GOLD,true);meter.setGravity(Gravity.CENTER);card(meter,0xFF123C91);
        addButton("Back to levels",this::showModuleFive);
    }
    private void showModuleFiveGame(){
        if(!canEnterLevel(5,moduleLevel)){
            moduleLevel=unlockedLevel(5);
            message("Level locked! Complete the previous level to unlock this one.");
            showModuleFive();
            return;
        }
        refreshHearts();if(!isPremium()&&hearts<=0){showModuleFiveHeartRefill();return;}
        int step=Math.max(0,Math.min(9,prefs.getInt("m5_step_"+moduleLevel,0)));
        speakQuestionOnce("m5-"+moduleLevel+"-"+step,"Study the word and choose the correct answer.");
        String word=m5Word(moduleLevel,step);
        String example="I want to use the word "+word+" in a real conversation.";
        List<String> options=new ArrayList<>();options.add(word);
        for(int j=1;j<4;j++){String candidate=m5Word(((moduleLevel-1+j*7)%100)+1,(step+j*3)%10);while(options.contains(candidate))candidate=m5Word(((moduleLevel+j*11)%100)+1,(step+j*5+1)%10);options.add(candidate);}
        Collections.shuffle(options,new Random(moduleLevel*211L+step*23L));
        base("Module 5 • Level "+moduleLevel,1);
        LinearLayout stats=row();TextView heart=txt("❤️  "+hearts+" / 5",16,GOLD,true);stats.addView(heart,new LinearLayout.LayoutParams(0,-2,1));stats.addView(txt("⭐  "+xp+" XP",15,WHITE,true));content.addView(stats);
        TextView progress=txt("QUESTION "+(step+1)+" OF 10   •   REAL-LIFE ENGLISH",14,GOLD,true);progress.setGravity(Gravity.CENTER);content.addView(progress);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(10);bar.setProgress(step);content.addView(bar,new LinearLayout.LayoutParams(-1,dp(10)));space(content,10);
        OwlMascotView mascot=new OwlMascotView();LinearLayout.LayoutParams mascotParams=new LinearLayout.LayoutParams(dp(205),dp(190));mascotParams.gravity=Gravity.CENTER;content.addView(mascot,mascotParams);
        TextView birdLine=txt("Привет! Продолжаем наше приключение. Прочитай слово, послушай пример и выбери правильный ответ. Я сразу проверю!",16,WHITE,true);birdLine.setGravity(Gravity.CENTER);birdLine.setPadding(dp(14),dp(12),dp(14),dp(12));card(birdLine,0xFF1455B5);
        TextView world=txt(m5Art[(moduleLevel-1)/10]+"\n"+m5Worlds[(moduleLevel-1)/10],23,WHITE,true);world.setGravity(Gravity.CENTER);card(world,0xFFB65B27);
        TextView wordCard=txt(word.toUpperCase(Locale.US),29,0xFF10265B,true);wordCard.setGravity(Gravity.CENTER);wordCard.setPadding(dp(14),dp(18),dp(14),dp(18));wordCard.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,24));wordCard.setElevation(dp(5));content.addView(wordCard);
        TextView meaning=txt("MEANING\nLooking up a reliable dictionary definition…\n\nExample: “"+example+"”",16,WHITE,false);meaning.setGravity(Gravity.CENTER);card(meaning,0xFF1765A0);lookupDictionaryMeaning(word,meaning);
        addButton("🔊 Listen to the word",()->speak(word));addButton("🔊 Listen to the example",()->speak(example));
        TextView prompt=txt("WORD CHALLENGE\nChoose the word you just studied.",16,GOLD,true);prompt.setGravity(Gravity.CENTER);content.addView(prompt);
        for(String option:options){final String picked=option;TextView b=txt(option,18,0xFF10265B,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(12),dp(15),dp(12),dp(15));b.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,22));b.setElevation(dp(3));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(8);content.addView(b,lp);b.setOnClickListener(v->{
            refreshHearts();if(!isPremium()&&hearts<=0){showModuleFiveHeartRefill();return;}
            if(picked.equals(word)){
                    speakRussian("Правильно! Отличная работа! Ты выбрал верный ответ.");
                if(step<9){prefs.edit().putInt("m5_step_"+moduleLevel,step+1).apply();addXp(7);addGems(1);dailyDone++;saveState();new AlertDialog.Builder(this).setTitle("🐦 Отлично! Правильно!").setMessage("Верно! Ты выбрал правильное слово. Так держать!\n\n"+word+"\n"+example+"\n\n+7 XP • +1 Gem").setPositiveButton("NEXT QUESTION",(d,w)->showModuleFiveGame()).show();}
                else{prefs.edit().putInt("m5_step_"+moduleLevel,0).putInt("module5_unlocked",Math.max(unlockedLevel(5),Math.min(100,moduleLevel+1))).apply();addXp(20);addGems(4);dailyDone++;saveState();new AlertDialog.Builder(this).setTitle("🐦 LEVEL COMPLETE! 🏆").setMessage("Прекрасная работа! Ты прошёл все 10 вопросов этого уровня.\n\n+20 XP • +4 Gems").setPositiveButton(moduleLevel<100?"NEXT LEVEL":"FINISH MODULE",(d,w)->{if(moduleLevel<100){moduleLevel++;showModuleFiveGame();}else{prefs.edit().putInt("module6_unlocked",Math.max(1,prefs.getInt("module6_unlocked",0))).apply();message("Fantastic! You completed all 100 levels of Module 5. Module 6 is unlocked!");showLearn();}}).show();}
            }else{
                speakRussian("Пока неверно. Не переживай, ошибки помогают учиться. Попробуй ещё раз.");
                consumeHeart();if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();saveState();
                if(hearts==0){new AlertDialog.Builder(this).setTitle("🐦 Сделаем паузу").setMessage("Ответ неверный, и все сердечки закончились. Одно сердечко восстановится через 30 минут. Отдохни немного — я буду ждать тебя!\n\nПравильный ответ: "+word).setPositiveButton("OK",(d,w)->showModuleFiveHeartRefill()).show();}
                else{new AlertDialog.Builder(this).setTitle("🐦 Почти получилось!").setMessage("Пока неверно, но не сдавайся!\n\nПравильный ответ: "+word+"\nОсталось сердечек: "+hearts+" / 5").setPositiveButton("TRY AGAIN",(d,w)->showModuleFiveGame()).show();}
            }
        });}
        TextView footer=txt("10 QUESTIONS PER LEVEL  •  LEARN → LISTEN → PLAY → UNLOCK",13,0xFFDCE8FF,false);footer.setGravity(Gravity.CENTER);content.addView(footer);
    }

    // Module 6: 100 levels, 10 fresh words per level, with guided study and multiple-choice checks.
    private final String[] m6Worlds={"Advanced Vocabulary 1","Advanced Vocabulary 2","Academic Language 1","Academic Language 2","Abstract Concepts","Precise Expression","Analysis & Argument","Research Language","Complex Reading","Mastery Challenge"};
    private final String[] m6Art={"🧠 💡 🔍","🔬 🧪 🌌","🌍 🏛️ 🎭","🌿 🌦️ 🐾","📊 💼 💹","📚 🎨 🎼","🤖 💻 ⚙️","🧑‍🤝‍🧑 💬 ❤️","⚖️ 🏛️ 📜","✍️ 🗣️ 🎓"};
    private final String[] m6Bank=(
        "abandons,abatement,abbey,abducting,aberdeen,abnegation,abnormality,abode,abortion,abound,absorbing,abstained,abstaining,abusive,abutted,accelerate,accelerated,accents,accentuating,acclaimed,accomplice,accomplishing,accusations,accuses,acidosis,acme,acorn,acquiesce,actionable,adamant,adamson,adapting,adequately,adheres,adhesion,adjust,admiralty,admonish,adorable,adoring,adulation,aerobic,aesthetic,affable,affirmed,affliction,aflame,afloat,agate,aggravating,aggravation,aggregation,aggressor,aggressors,agonies,agonizing,agrees,aids,aimlessly,airplane,aisle,alacrity,alamance,alamo,alba,albeit,aldermen,alertness,alexanders,algebra,alienating,aline,allaying,allays,allegro,alleviate,alleviating,aloft,aloofness,alps," +
        "alright,alsace,alsatian,altars,alternating,alton,aluminium,amalgam,amazingly,ambiguity,ambiguous,ambulatory,amend,amended,amending,amidst,amie,amour,amphitheater,amuses,anaerobe,anaesthetist,analyzing,anderson,andre,andrews,angelic,angling,angular,annihilated,announcements,annoy,antagonistic,antecedents,antigens,antitoxins,antoinette,anxieties,apace,apologise,apologizing,apostle,appendicitis,appetizing,apples,appliance,appraise,appreciably,appreciating,apprehend,apprentice,apprentices,approximating,arabia,arabian,arbitrate,arbuthnot,archie,architects,architectural,arctic,aristocrat,arithmetic,armada,arming,armour,aromatic,arrayed,arrogance,arrows,arson,asch,ashen,ashy,asparagus,aspirin,assaulted,asserting,assessment,asset," +
        "assiduous,assignment,assisting,associating,assumptions,assurances,assuredly,astoria,asunder,athlete,athletes,athletic,athwart,atrocious,atrophied,attacker,attributing,augment,augusta,augustin,auspices,austin,austro,authoritative,automatically,autonomous,avalanche,avarice,avenged,averaging,avoidance,awareness,azure,babcock,babe,bachelors,backing,backwardness,backwash,backwoods,baden,badges,baffling,bagging,baggy,bailiff,bait,bake,balances,bales,balloons,balsam,bancroft,bandit,banging,banishment,bankruptcies,banter,bantering,baptism,baptized,barbarian,barbaric,barbarity,barbarous,barest,bargained,barges,barracks,barricaded,barrington,bartering,bartholomew,basal,baser,bashful,basil,basins,bastard,bated," +
        "bathroom,battled,battleship,baying,beak,bearer,beau,beaucoup,beauties,beckoning,bedded,bedford,bedridden,beetles,befell,befitting,beget,behaves,belabored,belfry,belgian,beliefs,belleau,belted,belts,bends,beneficiary,bengal,berth,berths,bets,betterment,beverage,beveridge,bias,bidding,bids,bien,bilious,biological,biology,bipolar,birthplace,birthright,bismarck,bitterest,blackest,blacksmith,blazers,bleached,bleeds,blink,blinked,bloated,blockades,bloodstains,blossoming,blossoms,blot,blotting,bluntness,boatload,bodices,bodyguard,boldest,bomb,bonne,bons,bonus,bookcase,bookish,boost,booted,bordering,bosse,botany,bothering,bottoms,boulevards,bourbon," +
        "bourgeois,bout,bouts,bowie,bowls,boxers,bracelets,bradford,bradshaw,bragg,bragging,braided,brake,bramwell,brandishing,brassy,braver,brawny,brazier,breaches,breakers,breakfasts,breasts,breech,bribe,bricklayers,bridal,briefest,brightens,brilliance,bristling,broaden,bruin,brunt,brutally,buck,budding,buffets,bugle,bulb,bulge,bulged,bulges,bulging,bulldog,bullion,bulwark,buoyant,burdening,burial,burri,burrowing,burrows,busied,businessmen,butterfly,buttermilk,buxton,buyer,buyers,byrom,cabal,cadaver,caissons,calculating,calcutta,calder,calendar,callers,calming,calvin,cameo,camera,canadians,candidacy,canning,cantata,canton,capabilities,capers," +
        "capitol,captivated,captors,captures,caravans,carefree,carlo,caroline,carrier,carron,carrot,carta,carte,cartridge,carve,carving,cashier,cassel,cassock,caste,castor,casualty,cataclysm,catalogue,cathcart,cats,caucasus,cauldron,causal,cavern,caverns,ceasing,cedar,ceding,cela,celerity,cemetery,censor,centralized,cereals,certificate,certify,challenges,championship,characterize,charon,charts,chasing,chasm,chatting,cheapest,cheaply,cheering,chef,cheque,cherishing,chieftain,chilled,chills,chimes,chink,chirping,choicest,choirs,choke,choleric,chopping,chronological,chucked,chuckle,circumcision,circumference,circumspect,circumvented,cirrhosis,civility,clandestine,clapper,clara,clarendon," +
        "clarify,clashes,classed,cleanest,clearest,clears,cleaver,cleverly,cliff,clinch,clipping,clips,closest,clown,clubbing,clumsily,coagulating,coated,cobb,cocking,coerce,coerced,coexist,cognition,cognizance,coherent,coiled,coils,coinciding,colder,coley,collars,collector,colloidal,colombian,colossus,colt,comedian,comma,commend,commended,commensurate,commentaries,commented,commodity,commoners,communal,compacts,companionship,compares,compartments,compassionate,competence,competing,competitor,completing,complexity,complicate,complicates,complied,compresses,compromised,concede,concisely,concourse,concurrent,conde,conditioned,confers,confiscate,confounded,confronting,congealed,congenial,conjugal,connivance,conserving,consolations,consolidate,conspiracies," +
        "constabulary,constricted,consumers,consummate,contentions,contiguous,contractor,contradicting,contrasting,contrive,controversial,converging,conversed,conveys,convoys,conway,coolidge,cooperated,copenhagen,coppers,coppery,corbin,cordon,cornell,cornwall,corpora,correctness,correlation,corrigan,corroboration,corrupted,corruptly,cortes,councilor,counseled,counsellor,countenances,counteracted,countered,counts,coup,coupon,courageous,couriers,courting,courtship,covenants,coventry,coverings,cowards,crab,cramped,cranium,crank,cranks,crass,creamy,creates,creative,creditor,creed,crib,cringe,cringing,cristal,criterion,croix,crouch,crouching,crow,crowning,crowns,crucify,crudele,crumbled,crumbles,crusted,crutches,cruz,cuff," +
        "culmination,culprits,cummins,cumulative,currently,curriculum,curt,curtailed,curtly,cutlets,cutters,cyanide,cygne,dairy,dakin,dale,damask,damning,dandy,dank,danzig,daren,darkly,darlings,dart,dawning,daydreams,deadliest,debut,decency,deceptions,deceptive,declaimed,decreased,defeating,defection,defer,deferring,defied,defines,deft,degenerates,delaying,delegations,deleterious,deletions,delights,delivers,deluded,deluge,demagogues,dementia,demon,demyan,dentine,dentist,dentition,denver,departures,deploying,depose,deprecated,depredations,depress,deputies,deranged,derisive,descends,descriptive,desecrate,deserting,desertion,deserving,desirability,despaired,despicable,despondent,detection,deter,detriment," +
        "developmental,deviate,deviating,devolve,devoting,devotional,devouring,diagnose,diagnosing,dialect,dialogue,diametrically,dice,dickens,dictator,dictionary,dietetic,digitalis,dignitaries,dilapidated,diligence,diminutive,dingy,dipped,directness,disagreed,disavow,disavowing,disbelieve,discard,disciple,disconcert,discount,discredit,discriminate,disdainful,disengaging,disillusioned,disinclined,disinfect,disintegrate,disintegrates,disloyal,dismally,dismembered,disobeying,disowned,dispensed,displease,disrespectful,dissections,dissension,dissuade,distend,distinguishes,disturbs,disunion,diverticula,diverticulum,diverting,divining,dizzy,docs,dodge,doers,doffing,domes,domestics,dominican,donelson,donor,doorways,dormant,doubles,doze,drafts,drags,draining,drains,draped," +
        "draughts,drawback,drawled,draws,dreamer,dressmaker,drinkers,drip,drumming,dubbed,dubiously,dublin,duelist,duels,dugout,dulled,dump,duodenum,duquesne,dwarfed,dwarfs,dwellers,dwindled,dwyer,dyed,dynamite,dynasty,dysentery,earthen,eccentricity,echoes,eclipsed,eclipses,economically,economists,eddy,edifying,edith,editorial,educate,effectiveness,efferent,effusive,eighties,elation,elbowed,elicit,elihu,elite,elkins,ellipse,emanating,embellish,embezzlement,emblem,emboldened,emerald,emerges,emigrated,emigre,eminently,emissary,emma,emphasizing,employments,employs,emptiness,emulating,emulation,enchantment,enclosing,endeavored,endo,enfant,enforcing,engineering,engines,enjoin,enlarges,enlighten," +
        "enlivened,ennui,enrich,ensign,entreating,entrust,entrusting,envied,envoys,epigram,epilogue,episcopalian,episodes,epochs,equaled,equaling,equation,equations,erodes,eroding,erred,erroneously,esch,essay,essentials,esteemed,estimable,estimation,eucalyptus,eustachian,evade,eventual,eventuality,everett,evergreen,evidenced,evince,evolutionary,exacted,exaggerate,exasperating,excavation,excavations,excitable,excretory,excusing,exemption,exemptions,exfoliation,exhibition,exhilarating,exhort,exhortation,exotic,experimenting,experts,expiate,expletives,explode,exploded,exploding,exploited,explorer,explorers,exported,exposes,exterminate,extinct,extorted,extortion,extraction,extracts,extravagance,extremists,exude,exulting,facade,faceted,facilitating,facto," +
        "fades,fads,faintness,fairer,fairyland,fallopian,fallow,falsely,famed,fancier,farmhouses,farthing,fashionably,fastening,fatigued,fatigues,faust,feasting,featureless,federated,fedor,fenton,ferret,fervent,fervently,fettered,feudalism,fevers,fiance,fibre,fictitious,fights,filial,filing,fingertips,finnish,fisherman,fishermen,fissure,flagella"
    ).split(",");
    private String m6Word(int level,int step){return m6Bank[(level-1)*10+step].trim();}
    private String m6Example(String word,int level,int step){
        String[] frames={"The report uses the word %s in its explanation.","I added %s to my advanced vocabulary notebook.","The speaker pronounced %s clearly during the discussion.","Can you explain how to use %s in a sentence?", "I noticed the word %s while reading an article.","Our class practised the pronunciation of %s today.","She used %s when presenting her research.","Try to recognise %s when you hear it in context.","Write a new sentence that includes %s.","Review %s again before moving to the next word."};
        return String.format(Locale.US,frames[(level+step)%frames.length],word);
    }
    private void showModuleSix(){
        base("Module 6 — Advanced Word Studio",1);
        TextView intro=txt("🦉\nDevelop advanced vocabulary with dictionary definitions, pronunciation, contextual examples and a four-option knowledge check.",17,WHITE,true);intro.setGravity(Gravity.CENTER);card(intro,0xFF6743B5);
        int unlocked=unlockedLevel(6);
        for(int start=1;start<=100;start+=10){int world=(start-1)/10;heading("WORLD "+(world+1)+" • "+m6Worlds[world]);TextView art=txt(m6Art[world],34,WHITE,true);art.setGravity(Gravity.CENTER);card(art,0xFF6743B5);LinearLayout grid=row();
            for(int n=start;n<start+10;n++){final int target=n;TextView cell=txt((n<=unlocked?"🧠":"🔒")+"\n"+n,13,n<=unlocked?WHITE:0xFFB9C7E8,true);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(3),dp(9),dp(3),dp(9));cell.setBackground(gradient(n<=unlocked?0xFF8057D5:0xFF46577E,0xFF33205E,14));cell.setElevation(dp(8));cell.setTranslationZ(dp(2));((GradientDrawable)cell.getBackground()).setStroke(dp(2),0x88FFFFFF);LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(0,dp(58),1);gp.setMargins(dp(2),dp(2),dp(2),dp(2));grid.addView(cell,gp);cell.setOnClickListener(v->{if(target<=unlocked){moduleLevel=target;showModuleSixGame();}else message("🔒 Locked! Pass Level "+(target-1)+" to unlock Level "+target+".");});if(n%5==0&&n<start+9){content.addView(grid);grid=row();}}content.addView(grid);
        }
    }
    // Retrieves a real dictionary definition for the current word. The lookup runs off the UI thread.
    private void lookupDictionaryMeaning(String word,TextView target){
        Thread worker=new Thread(()->{
            String definition=null;
            HttpURLConnection connection=null;
            try{
                URL url=new URL("https://api.dictionaryapi.dev/api/v2/entries/en/"+URLEncoder.encode(word,"UTF-8"));
                connection=(HttpURLConnection)url.openConnection();connection.setRequestMethod("GET");connection.setConnectTimeout(7000);connection.setReadTimeout(7000);
                if(connection.getResponseCode()==200){
                    ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=connection.getInputStream()){byte[] buffer=new byte[4096];int n;while((n=in.read(buffer))!=-1)out.write(buffer,0,n);}
                    JSONArray entries=new JSONArray(out.toString("UTF-8"));
                    outer: for(int i=0;i<entries.length();i++){JSONArray meanings=entries.getJSONObject(i).optJSONArray("meanings");if(meanings==null)continue;for(int j=0;j<meanings.length();j++){JSONArray defs=meanings.getJSONObject(j).optJSONArray("definitions");if(defs!=null&&defs.length()>0){String d=defs.getJSONObject(0).optString("definition","").trim();if(!d.isEmpty()){String pos=meanings.getJSONObject(j).optString("partOfSpeech","");definition=(pos.isEmpty()?"":"("+pos+") ")+d;break outer;}}}}
                }
            }catch(Exception ignored){}finally{if(connection!=null)connection.disconnect();}
            final String result=definition;
            runOnUiThread(()->{if(isFinishing()||target.getParent()==null)return;if(result!=null)target.setText("MEANING\n"+result+"\n\n(Source: English dictionary)");else target.setText("MEANING\nDefinition is unavailable offline. Connect to the internet and tap the word again later.");});
        });worker.setDaemon(true);worker.start();
    }

    private void showModuleSixHeartRefill(){
        refreshHearts();
        base("Energy is empty",1);
        OwlMascotView mascot=new OwlMascotView();
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(dp(205),dp(190));
        mp.gravity=Gravity.CENTER;content.addView(mascot,mp);
        TextView talk=txt("Сердечки закончились. Сделай небольшой перерыв — следующее сердечко появится через 30 минут. Я буду ждать тебя!",17,WHITE,true);
        talk.setGravity(Gravity.CENTER);card(talk,0xFF1455B5);
        TextView meter=txt("❤️  "+hearts+" / 5\n\n"+heartWaitText(),20,GOLD,true);
        meter.setGravity(Gravity.CENTER);card(meter,0xFF123C91);
        addButton("Back to levels",this::showModuleSix);
    }

    private void showModuleSixGame(){
        if(!canEnterLevel(6,moduleLevel)){
            moduleLevel=unlockedLevel(6);
            message("Level locked! Complete the previous level to unlock this one.");
            showModuleSix();
            return;
        }
        refreshHearts();
        if(!isPremium()&&hearts<=0){showModuleSixHeartRefill();return;}
        int step=Math.max(0,Math.min(9,prefs.getInt("m6_step_"+moduleLevel,0)));
        speakQuestionOnce("m6-"+moduleLevel+"-"+step,"Study the word and choose the correct answer.");
        String word=m6Word(moduleLevel,step);
        String example=m6Example(word,moduleLevel,step);
        List<String> options=new ArrayList<>();options.add(word);
        int idx=(moduleLevel-1)*10+step;
        for(int j=1;j<4;j++){
            String candidate=m6Bank[(idx+j*73+41)%m6Bank.length].trim();
            while(options.contains(candidate))candidate=m6Bank[(idx+j*97+17)%m6Bank.length].trim();
            options.add(candidate);
        }
        Collections.shuffle(options,new Random(moduleLevel*307L+step*31L));
        base("Module 6 • Level "+moduleLevel,1);
        LinearLayout stats=row();
        TextView heart=txt("❤️  "+hearts+" / 5",16,GOLD,true);
        stats.addView(heart,new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(txt("⭐  "+xp+" XP",15,WHITE,true));content.addView(stats);
        TextView progress=txt("QUESTION "+(step+1)+" OF 10   •   ADVANCED ENGLISH",14,GOLD,true);
        progress.setGravity(Gravity.CENTER);content.addView(progress);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        bar.setMax(10);bar.setProgress(step);content.addView(bar,new LinearLayout.LayoutParams(-1,dp(10)));space(content,10);
        OwlMascotView mascot=new OwlMascotView();
        LinearLayout.LayoutParams mascotParams=new LinearLayout.LayoutParams(dp(205),dp(190));
        mascotParams.gravity=Gravity.CENTER;content.addView(mascot,mascotParams);
        TextView birdLine=txt("Привет! Продолжаем наше приключение. Изучи слово, послушай произношение и выбери правильный ответ. Я сразу проверю!",16,WHITE,true);
        birdLine.setGravity(Gravity.CENTER);birdLine.setPadding(dp(14),dp(12),dp(14),dp(12));card(birdLine,0xFF1455B5);
        TextView world=txt(m6Art[(moduleLevel-1)/10]+"\n"+m6Worlds[(moduleLevel-1)/10],23,WHITE,true);
        world.setGravity(Gravity.CENTER);card(world,0xFF6743B5);
        TextView wordCard=txt(word.toUpperCase(Locale.US),29,0xFF10265B,true);
        wordCard.setGravity(Gravity.CENTER);wordCard.setPadding(dp(14),dp(18),dp(14),dp(18));wordCard.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,24));wordCard.setElevation(dp(5));content.addView(wordCard);
        TextView meaning=txt("MEANING\nLooking up the dictionary definition…",16,WHITE,false);
        meaning.setGravity(Gravity.CENTER);card(meaning,0xFF1765A0);lookupDictionaryMeaning(word,meaning);
        TextView lesson=txt("EXAMPLE SENTENCE\n“"+example+"”",16,WHITE,false);
        lesson.setGravity(Gravity.CENTER);card(lesson,0xFF1765A0);
        addButton("🔊 Listen to the word",()->speak(word));
        addButton("🔊 Listen to the example",()->speak(example));
        TextView prompt=txt("WORD CHALLENGE\nChoose the word you have just studied.",16,GOLD,true);
        prompt.setGravity(Gravity.CENTER);content.addView(prompt);
        for(String option:options){
            final String picked=option;
            TextView b=txt(option,18,0xFF10265B,true);b.setGravity(Gravity.CENTER);
            b.setPadding(dp(12),dp(15),dp(12),dp(15));
            b.setBackground(gradient(0xFFFFFFFF,0xFFEAF3FF,22));b.setElevation(dp(3));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(8);
            content.addView(b,lp);
            b.setOnClickListener(v->{
                refreshHearts();if(!isPremium()&&hearts<=0){showModuleSixHeartRefill();return;}
                if(picked.equals(word)){
                    speakRussian("Правильно! Отличная работа! Ты выбрал верный ответ.");
                    if(step<9){
                        prefs.edit().putInt("m6_step_"+moduleLevel,step+1).apply();
                        addXp(7);addGems(1);dailyDone++;saveState();
                        new AlertDialog.Builder(this).setTitle("🐦 Отлично! Правильно!")
                            .setMessage("Верно! Ты правильно выбрал слово. Молодец!\n\n"+word+"\n"+example+"\n\n+7 XP • +1 Gem")
                            .setPositiveButton("NEXT QUESTION",(d,w)->showModuleSixGame()).show();
                    }else{
                        prefs.edit().putInt("m6_step_"+moduleLevel,0)
                            .putInt("module6_unlocked",Math.max(unlockedLevel(6),Math.min(100,moduleLevel+1))).apply();
                        addXp(20);addGems(4);dailyDone++;saveState();
                        new AlertDialog.Builder(this).setTitle("🐦 LEVEL COMPLETE! 🏆")
                            .setMessage("Прекрасная работа! Ты ответил на все 10 вопросов этого уровня.\n\n+20 XP • +4 Gems")
                            .setPositiveButton(moduleLevel<100?"NEXT LEVEL":"FINISH MODULE",(d,w)->{
                                if(moduleLevel<100){moduleLevel++;showModuleSixGame();}
                                else{message("Amazing! You completed all 100 levels of Module 6!");showLearn();}
                            }).show();
                    }
                }else{
                    speakRussian("Пока неверно. Не сдавайся! Ошибки помогают учиться. Попробуй ещё раз.");
                    consumeHeart();
                    if(hearts<5&&prefs.getLong("heart_timer_anchor",0L)==0L)
                        prefs.edit().putLong("heart_timer_anchor",System.currentTimeMillis()).apply();
                    saveState();
                    if(hearts==0){
                        new AlertDialog.Builder(this).setTitle("🐦 Сделаем паузу")
                            .setMessage("Ответ неверный, и все сердечки закончились. Одно сердечко восстановится через 30 минут. Отдохни немного — я буду ждать тебя!\n\nПравильный ответ: "+word)
                            .setPositiveButton("OK",(d,w)->showModuleSixHeartRefill()).show();
                    }else{
                        new AlertDialog.Builder(this).setTitle("🐦 Почти получилось!")
                            .setMessage("Пока неверно, но не сдавайся!\n\nПравильный ответ: "+word+"\nОсталось сердечек: "+hearts+" / 5")
                            .setPositiveButton("TRY AGAIN",(d,w)->showModuleSixGame()).show();
                    }
                }
            });
        }
    }

    private void showLesson(){base("Lesson "+(lessonNo+1)+" / 10",1);StringBuilder heartLine=new StringBuilder();for(int h=0;h<hearts;h++)heartLine.append("❤️ ");TextView meta=txt(heartLine.toString()+"     "+levels[level],14,GOLD,true);content.addView(meta);space(content,12);TextView picture=txt(lessonNo%5==0?"🍎":lessonNo%5==1?"👩‍🎓":lessonNo%5==2?"🏫":lessonNo%5==3?"❄️":"🌅",78,WHITE,true);picture.setGravity(Gravity.CENTER);picture.setBackground(shape(WHITE,24));content.addView(picture,new LinearLayout.LayoutParams(-1,dp(160)));space(content,14);TextView q=txt(lessonQuestions[lessonNo%lessonQuestions.length],21,WHITE,true);q.setGravity(Gravity.CENTER);content.addView(q);space(content,8);String[] options=lessonOptions[lessonNo%lessonOptions.length];for(int i=0;i<options.length;i++){final int answer=i;TextView opt=txt(options[i],17,0xFF10265B,true);opt.setGravity(Gravity.CENTER);opt.setPadding(dp(12),dp(14),dp(12),dp(14));opt.setBackground(shape(WHITE,22));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(8);content.addView(opt,p);opt.setOnClickListener(v->checkAnswer(answer));} }
    private void checkAnswer(int answer){if(answer==lessonAnswers[lessonNo%lessonAnswers.length]){speakRussian("Правильно! Отличная работа!");addXp(10);addGems(1);dailyDone++;lessonNo=(lessonNo+1)%10;saveState();new AlertDialog.Builder(this).setTitle("Excellent! 🎉").setMessage("Correct answer! +10 XP and +1 gem.").setPositiveButton("Continue",(d,w)->showLesson()).show();}else{speakRussian("Не совсем верно. Продолжай тренироваться, у тебя получится!");consumeHeart();saveState();new AlertDialog.Builder(this).setTitle("Not quite").setMessage("Correct answer: "+lessonOptions[lessonNo%lessonOptions.length][lessonAnswers[lessonNo%lessonAnswers.length]]+". Keep practicing!").setPositiveButton("Try again",(d,w)->showLesson()).show();}}
    private void featureBase(String title,String sub,int tab){base(title,tab);TextView s=txt(sub,15,0xFFDCE8FF,false);content.addView(s);space(content,10);}
    private void showVocabulary(){featureBase("Vocabulary","Build your word power — tap a word to hear it.",-1);for(int i=0;i<vocab.length;i++){final String word=vocabSpeak[i];TextView t=txt("📖   "+vocab[i]+"     🔊",18,WHITE,true);card(t,0xFF153C91);t.setOnClickListener(v->{if(tts!=null)tts.speak(word,TextToSpeech.QUEUE_FLUSH,null,"vocab");});}}
    private void showGrammar(){featureBase("Grammar","Learn a rule, then practice it.",-1);TextView rule=txt("PRESENT SIMPLE\n\nUse the present simple for habits and facts.\n\nI play • You play • She plays\n\nExample: She works every day.",18,WHITE,false);card(rule,0xFF153C91);addButton("Practice this rule",this::showLesson);}
    private void showListening(){featureBase("Listening","Listen carefully and repeat the sentence.",-1);String sentence="Hello! My name is Alex. I am learning English.";TextView t=txt("🎧\n\n\""+sentence+"\"",21,WHITE,true);t.setGravity(Gravity.CENTER);card(t,0xFF153C91);addButton("▶  Play audio",()->speak(sentence));addButton("I understood — take a quiz",this::showLesson);}
    private void speak(String s){speakEnglish(s);}
    private void speakEnglish(String text){if(tts==null||!ttsReady||text==null||text.trim().isEmpty())return;int result=tts.setLanguage(Locale.US);if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED)return;tts.setSpeechRate(0.88f);tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"lingexa_en");}
    private void speakRussian(String text){if(tts==null||!ttsReady||text==null||text.trim().isEmpty())return;int result=tts.setLanguage(new Locale("ru","RU"));if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){speakEnglish(text);return;}tts.setSpeechRate(0.86f);tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"lingexa_ru");}
    private void speakQuestionOnce(String key,String text){if(ttsReady&&!key.equals(lastSpokenPrompt)){lastSpokenPrompt=key;speakEnglish(text);}}

    private int speakingUnlocked(){int u=1;for(int i=0;i<speakingStages.length-1;i++){if(prefs.getInt("speaking_step_"+i,0)>=10)u=i+2;else break;}return u;}
    private void showSpeakingCourse(){
        base("Speaking Journey",3);
        TextView owl=txt("🦉🎓\nYour speaking coach",30,WHITE,true);owl.setGravity(Gravity.CENTER);card(owl,0xFF174FBD);
        TextView intro=txt("Listen, repeat, and speak step by step. The owl will guide you in Russian while you practise English.",15,0xFFDCEBFF,false);card(intro,0xFF153C91);
        int unlocked=speakingUnlocked();
        for(int i=0;i<speakingStages.length;i++){
            final int stage=i;int done=prefs.getInt("speaking_step_"+i,0);boolean open=i<unlocked;
            TextView tile=txt((open?"🗣️  ":"🔒  ")+speakingStages[i]+"\n"+(open?done+" / 10 speaking lessons":"Complete the previous stage to unlock"),18,WHITE,true);tile.setPadding(dp(16),dp(14),dp(16),dp(14));tile.setBackground(gradient(open?0xFF167BEF:0xFF52678D,open?0xFF123B9D:0xFF293B61,20));tile.setElevation(dp(5));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(10);content.addView(tile,lp);tile.setOnClickListener(v->{if(stage<speakingUnlocked()){speakingStage=stage;speakingStep=prefs.getInt("speaking_step_"+stage,0);showSpeakingLesson();}else message("Finish the previous stage to unlock this one.");});
        }
    }
    private void showSpeakingLesson(){
        if(speakingStep>=10){showSpeakingStageComplete();return;}
        base(speakingStages[speakingStage],3);
        int done=prefs.getInt("speaking_step_"+speakingStage,0);
        TextView progress=txt("LESSON "+(speakingStep+1)+" / 10     •     ❤️ Speaking practice",14,GOLD,true);progress.setGravity(Gravity.CENTER);content.addView(progress);space(content,10);
        TextView owl=txt("🦉🎓",66,WHITE,true);owl.setGravity(Gravity.CENTER);card(owl,0xFF174FBD);
        String prompt=speakingPrompts[speakingStage][speakingStep];
        TextView coach=txt("Сначала послушай фразу, затем повтори её вслух. Не торопись!",16,WHITE,true);coach.setGravity(Gravity.CENTER);card(coach,0xFF153C91);
        TextView phrase=txt("“"+prompt+"”",23,WHITE,true);phrase.setGravity(Gravity.CENTER);phrase.setPadding(dp(12),dp(18),dp(12),dp(18));card(phrase,0xFF164BA8);
        addButton("🔊  Listen to the phrase",()->speakEnglish(prompt));
        addButton("🐦  Owl says it slowly",()->{if(tts!=null){tts.setSpeechRate(0.68f);speakEnglish(prompt);tts.setSpeechRate(0.88f);}});
        addButton("🎙️  Your turn — speak",()->startSpeakingRecognition());
        addButton("↻  Listen again",()->speakEnglish(prompt));
        TextView hint=txt("Tip: Speak clearly. You can try again as many times as you need.",13,0xFFDCEBFF,false);hint.setGravity(Gravity.CENTER);content.addView(hint);
    }
    private void startSpeakingRecognition(){
        try{Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en-US");i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Repeat the English sentence");i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);startActivityForResult(i,78);}catch(Exception e){message("Speech recognition is unavailable. Check that a speech recognition service is installed.");}
    }
    private String normalizeSpeech(String value){return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9 ]"," ").replaceAll("\\s+"," ").trim();}
    private int speechSimilarity(String expected,String actual){String[] target=normalizeSpeech(expected).split(" ");String[] heard=normalizeSpeech(actual).split(" ");if(target.length==0||heard.length==0)return 0;int matches=0;for(String word:target){for(String h:heard){if(word.equals(h)){matches++;break;}}}return Math.round(100f*matches/target.length);}
    private void handleSpeakingResult(String said){
        String expected=speakingPrompts[speakingStage][speakingStep];int score=speechSimilarity(expected,said);
        boolean passed=score>=65;
        String feedback=passed?"Отлично! Ты хорошо повторил фразу. Продолжаем!":"Хорошая попытка! Послушай ещё раз и попробуй произнести фразу чётче.";
        speakRussian(feedback);
        String detail="You said: "+said+"\n\nTarget: "+expected+"\n\nMatch: "+score+"%\n\n"+feedback;
        new AlertDialog.Builder(this).setTitle(passed?"Great speaking! 🎉":"Try once more 🦉").setMessage(detail).setPositiveButton(passed?"Next lesson":"Try again",(d,w)->{if(passed){speakingStep++;prefs.edit().putInt("speaking_step_"+speakingStage,speakingStep).apply();addXp(8);dailyDone++;saveState();showSpeakingLesson();}else showSpeakingLesson();}).setNegativeButton("Listen again",(d,w)->{speakEnglish(expected);showSpeakingLesson();}).show();
    }
    private void showSpeakingStageComplete(){
        base("Stage complete!",3);TextView owl=txt("🦉🎓\nAmazing work!",42,GOLD,true);owl.setGravity(Gravity.CENTER);card(owl,0xFF174FBD);TextView msg=txt("You completed "+speakingStages[speakingStage]+". Your next speaking stage is ready!",19,WHITE,true);msg.setGravity(Gravity.CENTER);card(msg,0xFF153C91);addButton("Continue to next stage",()->showSpeakingCourse());addButton("Review this stage",()->{speakingStep=0;showSpeakingLesson();});
    }
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==78){if(result==RESULT_OK&&data!=null){ArrayList<String>a=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);String said=a==null||a.isEmpty()?"":a.get(0);if(said.isEmpty()){message("No speech detected. Please try again.");}else handleSpeakingResult(said);}else{message("Speech practice cancelled. Tap the microphone when you are ready.");}}else if(request==77&&result==RESULT_OK&&data!=null){ArrayList<String>a=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);String said=a==null||a.isEmpty()?"No speech detected.":a.get(0);new AlertDialog.Builder(this).setTitle("Speech result").setMessage(said).setPositiveButton("Done",null).show();}else if(request==79&&result==RESULT_OK&&data!=null&&data.getData()!=null){Uri uri=data.getData();try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}profileImageUri=uri.toString();prefs.edit().putString("profile_image_uri",profileImageUri).apply();profileAvatar="";prefs.edit().putString("profile_avatar",profileAvatar).apply();showProfile();}}
    private void showWriting(){featureBase("Writing","Write a sentence and save it on this device.",-1);EditText e=new EditText(this);e.setText(prefs.getString("writing",""));e.setHint("Write your sentence here...");e.setTextColor(WHITE);e.setHintTextColor(0xFFB9C9F5);e.setMinLines(5);e.setGravity(Gravity.TOP);e.setPadding(dp(14),dp(14),dp(14),dp(14));e.setBackground(shape(0xFF153C91,18));content.addView(e);space(content,12);addButton("Save writing",()->{prefs.edit().putString("writing",e.getText().toString()).apply();message("Your writing has been saved on this device.");});}
    private void showChallenges(){featureBase("Daily Challenges","Complete small missions to earn XP.",-1);String[] missions={"Learn 5 new words","Complete one lesson","Practice speaking for 2 minutes"};for(int i=0;i<missions.length;i++){final int id=i;boolean done=prefs.getBoolean("mission_"+i,false);TextView t=txt((done?"✅  ":"🎯  ")+missions[i]+"     +20 XP",17,WHITE,true);card(t,0xFF6936B5);t.setOnClickListener(v->{if(!prefs.getBoolean("mission_"+id,false)){prefs.edit().putBoolean("mission_"+id,true).apply();addXp(20);saveState();showChallenges();}else message("Challenge already completed today.");});}}
    private String currentSeasonName(){
        Calendar c=Calendar.getInstance();int month=c.get(Calendar.MONTH); // Calendar months are zero-based
        if(month>=2&&month<=4)return "SPRING";
        if(month>=5&&month<=7)return "SUMMER";
        if(month>=8&&month<=10)return "AUTUMN";
        return "WINTER";
    }
    private String currentSeasonKey(){Calendar c=Calendar.getInstance();int month=c.get(Calendar.MONTH);int year=c.get(Calendar.YEAR);String season=currentSeasonName();if("WINTER".equals(season)&&(month==0||month==1))year--;return season+"_"+year;}
    private int completedLevels(int module){return Math.max(0,Math.min(100,unlockedLevel(module)-1));}
    private void showRewards(){
        base("Seasonal Rewards",3);
        String season=currentSeasonName();String seasonKey=currentSeasonKey();
        String seasonIcon="SPRING".equals(season)?"🌸":"SUMMER".equals(season)?"☀️":"AUTUMN".equals(season)?"🍂":"❄️";
        TextView hero=txt(seasonIcon+"  "+season+" SEASON\nUnlock rewards in every module!",20,WHITE,true);hero.setGravity(Gravity.CENTER);card(hero,0xFF174FB5);
        TextView expl=txt("Complete levels to unlock seasonal prizes. Each module has its own reward path. Claim each prize once per season.",14,0xFFE5F1FF,false);expl.setPadding(dp(4),dp(2),dp(4),dp(12));content.addView(expl);
        String[] moduleNames={"First Steps","Everyday Adventures","Sentence Explorer","Vocabulary Quest","Real-Life Word Quest","Advanced Word Studio"};
        String[] icons={"🌱","🧭","🦉","📚","🎓","🚀"};
        int[] milestones={10,25,50,75,100};int[] gemRewards={5,10,20,30,50};int[] xpRewards={25,50,100,150,250};
        for(int m=1;m<=6;m++){
            int done=completedLevels(m);LinearLayout panel=col();panel.setPadding(dp(13),dp(13),dp(13),dp(10));panel.setBackground(gradient(0xFF1769D2,0xFF09245F,22));panel.setElevation(dp(5));
            TextView title=txt(icons[m-1]+"  MODULE "+m+" — "+moduleNames[m-1],16,0xFFFFE77A,true);panel.addView(title);
            TextView prog=txt(done+" / 100 levels completed",13,WHITE,true);prog.setPadding(0,dp(5),0,dp(6));panel.addView(prog);
            ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(100);bar.setProgress(done);panel.addView(bar,new LinearLayout.LayoutParams(-1,dp(8)));
            for(int i=0;i<milestones.length;i++){
                final int module=m, milestone=milestones[i], gemsReward=gemRewards[i], xpReward=xpRewards[i];
                String claimKey="season_reward_"+seasonKey+"_m"+module+"_l"+milestone;
                boolean claimed=prefs.getBoolean(claimKey,false);boolean unlocked=done>=milestone;
                LinearLayout reward=row();reward.setPadding(dp(8),dp(8),dp(8),dp(8));reward.setBackground(shape(unlocked?0xFF155C9E:0xFF344B78,14));
                TextView detail=txt((unlocked?(claimed?"✅":"🎁"):"🔒")+"  Level "+milestone+" prize\n     +"+gemsReward+" Gems  •  +"+xpReward+" XP",13,WHITE,true);reward.addView(detail,new LinearLayout.LayoutParams(0,-2,1));
                TextView action=txt(claimed?"CLAIMED":unlocked?"CLAIM":"LOCKED",11,unlocked&&!claimed?0xFF164000:0xFFDCE8FF,true);action.setGravity(Gravity.CENTER);action.setPadding(dp(10),dp(9),dp(10),dp(9));action.setBackground(gradient(unlocked&&!claimed?0xFFFFE45C:0xFF526A91,unlocked&&!claimed?0xFFFFAE16:0xFF30456E,16));reward.addView(action);
                if(unlocked&&!claimed){reward.setOnClickListener(v->{if(!prefs.getBoolean(claimKey,false)){prefs.edit().putBoolean(claimKey,true).apply();addGems(gemsReward);addXp(xpReward);saveState();message("Season prize unlocked! +"+gemsReward+" Gems and +"+xpReward+" XP");showRewards();}});}
                else if(!unlocked){reward.setOnClickListener(v->message("Complete Level "+milestone+" in Module "+module+" to unlock this seasonal prize."));}
                LinearLayout.LayoutParams rlp=new LinearLayout.LayoutParams(-1,-2);rlp.topMargin=dp(7);panel.addView(reward,rlp);
            }
            LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,-2);pp.bottomMargin=dp(13);content.addView(panel,pp);
        }
        TextView footer=txt("New seasonal reward paths rotate with Spring, Summer, Autumn and Winter. Your claimed prizes stay in your account.",12,0xFFD6E6FF,false);content.addView(footer);
    }

    private void showLeague(){featureBase("League","Weekly learning leaderboard — demo standings.",-1);String[] ranks={"🥇  Emma                         2,450 XP","🥈  Noah                          2,120 XP","🥉  "+learnerName+"                         "+xp+" XP","4.  Olivia                            980 XP"};for(String s:ranks){TextView t=txt(s,17,WHITE,true);card(t,0xFF153C91);}TextView note=txt("Your XP is real and stored locally. Other players are sample entries until an online leaderboard is connected.",13,0xFFCCD9FF,false);content.addView(note);}
    private void showProgress(){base("Your Progress",-1);TextView t=txt("🏅  "+levels[level]+"\n\n⭐  "+xp+" XP earned\n💎  "+gems+" gems\n🔥  "+streak+" day streak\n❤️  "+hearts+" hearts\n\nDaily goal     "+Math.min(dailyDone,5)+" / 5 lessons",20,WHITE,true);card(t,0xFF153C91);ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(5);bar.setProgress(Math.min(dailyDone,5));content.addView(bar,new LinearLayout.LayoutParams(-1,dp(12)));space(content,12);addButton("Continue learning",this::showLearn);}
    private void showAiTeacher(){featureBase("AI Teacher","Practice with your owl learning companion.",-1);TextView owl=txt("🦉🎓",72,WHITE,true);owl.setGravity(Gravity.CENTER);card(owl,0xFF153C91);TextView expl=txt("Start with a topic. The built-in demo gives you a speaking prompt and can read it aloud. A real conversational AI needs a secure server connection and API key.",16,WHITE,false);card(expl,0xFF153C91);String[] topics={"Introduce yourself","Order food at a restaurant","Talk about your daily routine","Ask for directions"};for(String topic:topics){TextView b=txt("💬  "+topic,17,WHITE,true);card(b,0xFF6936B5);b.setOnClickListener(v->{String prompt="Let's practice English. Topic: "+topic+". Please say: Hello, my name is Alex. What is your name?";speak(prompt);new AlertDialog.Builder(this).setTitle("Speaking prompt").setMessage(prompt+"\n\nTap Speaking Practice to answer aloud.").setPositiveButton("Start speaking",(d,w)->showSpeakingCourse()).setNegativeButton("Close",null).show();});}}
    private void showPremium(){
        base("LINGEXA PREMIUM",-1);
        boolean active=isPremium();
        LinearLayout hero=col();hero.setGravity(Gravity.CENTER);hero.setPadding(dp(18),dp(22),dp(18),dp(22));hero.setBackground(gradient(0xFF8D4BEB,0xFF321078,26));hero.setElevation(dp(9));
        TextView gem=txt("💎",58,WHITE,true);gem.setGravity(Gravity.CENTER);hero.addView(gem);
        TextView title=txt(active?"PREMIUM ACTIVE":"UNLOCK YOUR FULL POTENTIAL",20,WHITE,true);title.setGravity(Gravity.CENTER);hero.addView(title);
        TextView subtitle=txt(active?"Все Premium-возможности включены на этом устройстве.":"Больше свободы, практики и наград в LINGEXA.",14,0xFFF2E8FF,false);subtitle.setGravity(Gravity.CENTER);hero.addView(subtitle);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.bottomMargin=dp(14);content.addView(hero,hp);
        String[][] perks={{"❤️","Бесконечные сердца","Ошибки не останавливают обучение — сердца всегда 5/5."},{"🔓","Все 6 модулей","Доступ ко всем 100 уровням каждого из шести модулей."},{"⭐","Удвоенный XP","За правильные ответы и задания начисляется в 2 раза больше XP."},{"💎","Удвоенные Gems","За учебные награды начисляется в 2 раза больше Gems."},{"🚫","Без рекламы","Premium-режим не показывает рекламные блоки."},{"🦉","AI Teacher и Speaking","Расширенная практика и дополнительные задания — по мере подключения AI-сервиса."},{"📊","Подробный прогресс","Статистика, достижения и история обучения."}};
        for(String[] perk:perks){LinearLayout item=row();item.setGravity(Gravity.CENTER_VERTICAL);item.setPadding(dp(13),dp(12),dp(13),dp(12));item.setBackground(gradient(0xFF174A9C,0xFF0B2A69,18));TextView icon=txt(perk[0],27,WHITE,true);item.addView(icon,new LinearLayout.LayoutParams(dp(42),-2));LinearLayout words=col();words.addView(txt(perk[1],16,WHITE,true));words.addView(txt(perk[2],12,0xFFD7E7FF,false));item.addView(words,new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,-2);ip.bottomMargin=dp(8);content.addView(item,ip);}
        if(active){addButton("✅ Premium включён",()->message("Premium уже активен на этом устройстве."));addButton("Выключить Premium demo",()->new AlertDialog.Builder(this).setTitle("Отключить демо Premium?").setMessage("Бесконечные сердца и Premium-доступ будут отключены.").setNegativeButton("Отмена",null).setPositiveButton("Отключить",(d,w)->activatePremiumDemo(false)).show());}
        else{addButton("💎 Включить Premium (демо)",()->new AlertDialog.Builder(this).setTitle("Включить Premium demo?").setMessage("Это тестовый режим для проверки функций. Оплата не списывается, подписка и платежи пока не подключены.").setNegativeButton("Отмена",null).setPositiveButton("Включить",(d,w)->activatePremiumDemo(true)).show());}
        TextView note=txt("Важно: покупка и проверка реальной подписки пока не подключены. Кнопка демо нужна только для тестирования Premium-функций.",12,0xFFD6E6FF,false);note.setPadding(dp(4),dp(12),dp(4),dp(8));content.addView(note);
    }
    private void showProfile(){
        base("My Profile",4);
        LinearLayout hero=col();hero.setGravity(Gravity.CENTER);hero.setPadding(dp(16),dp(18),dp(16),dp(18));hero.setBackground(gradient(0xFF174FAE,0xFF08245F,26));hero.setElevation(dp(6));
        profilePhotoView=new ImageView(this);profilePhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);profilePhotoView.setBackground(shape(0xFF0B347F,100));profilePhotoView.setClipToOutline(true);LinearLayout.LayoutParams photoP=new LinearLayout.LayoutParams(dp(116),dp(116));photoP.gravity=Gravity.CENTER;hero.addView(profilePhotoView,photoP);refreshProfilePhoto();
        TextView photoHint=txt("Tap avatar to change photo",12,0xFFD9EAFF,false);photoHint.setGravity(Gravity.CENTER);LinearLayout.LayoutParams hintP=new LinearLayout.LayoutParams(-1,-2);hintP.topMargin=dp(8);hero.addView(photoHint,hintP);photoHint.setOnClickListener(v->showAvatarOptions());profilePhotoView.setOnClickListener(v->showAvatarOptions());
        TextView name=txt(learnerName,25,WHITE,true);name.setGravity(Gravity.CENTER);LinearLayout.LayoutParams nameP=new LinearLayout.LayoutParams(-1,-2);nameP.topMargin=dp(10);hero.addView(name,nameP);
        TextView levelLabel=txt("🏅  "+levels[Math.max(0,Math.min(level,levels.length-1))],15,0xFFFFE77A,true);levelLabel.setGravity(Gravity.CENTER);LinearLayout.LayoutParams levelP=new LinearLayout.LayoutParams(-1,-2);levelP.topMargin=dp(5);hero.addView(levelLabel,levelP);
        LinearLayout.LayoutParams heroP=new LinearLayout.LayoutParams(-1,-2);heroP.bottomMargin=dp(14);content.addView(hero,heroP);
        addButton("✏️  Edit name",()->{EditText input=new EditText(this);input.setSingleLine(true);input.setText(learnerName);new AlertDialog.Builder(this).setTitle("Your name").setView(input).setPositiveButton("Save",(d,w)->{learnerName=input.getText().toString().trim();if(learnerName.isEmpty())learnerName="Learner";saveState();showProfile();}).setNegativeButton("Cancel",null).show();});
        heading("YOUR LEARNING STATS");
        LinearLayout stats=row();stats.setGravity(Gravity.CENTER);addProfileStat(stats,"💎",String.valueOf(gems),"Gems");addProfileStat(stats,"⭐",String.valueOf(xp),"Total XP");addProfileStat(stats,"🔥",String.valueOf(streak),"Day streak");LinearLayout.LayoutParams statsP=new LinearLayout.LayoutParams(-1,-2);statsP.bottomMargin=dp(10);content.addView(stats,statsP);
        LinearLayout stats2=row();stats2.setGravity(Gravity.CENTER);addProfileStat(stats2,"❤️",String.valueOf(hearts),"Hearts");addProfileStat(stats2,"📚",String.valueOf(dailyDone),"Lessons today");addProfileStat(stats2,"🎯",(Math.min(dailyDone,5)+"/5"),"Daily goal");LinearLayout.LayoutParams stats2P=new LinearLayout.LayoutParams(-1,-2);stats2P.bottomMargin=dp(14);content.addView(stats2,stats2P);
        TextView levelInfo=txt("LEVEL PROGRESS\n"+levels[Math.max(0,Math.min(level,levels.length-1))]+"\nKeep learning to unlock the next stage!",15,WHITE,true);card(levelInfo,0xFF153C91);
        addButton("📈  Detailed learning progress",this::showProgress);addButton("🏆  Achievements & league",this::showLeague);addButton("🎯  Change daily learning goal",()->new AlertDialog.Builder(this).setTitle("Daily goal").setItems(new String[]{"3 lessons","5 lessons","10 lessons"},(d,w)->message("Daily goal setting will be available in a future update.")).show());
    }
    private void addProfileStat(LinearLayout parent,String icon,String value,String label){LinearLayout tile=col();tile.setGravity(Gravity.CENTER);tile.setPadding(dp(5),dp(12),dp(5),dp(12));tile.setBackground(gradient(0xFF174A9C,0xFF0B2A69,18));TextView i=txt(icon,23,WHITE,true);i.setGravity(Gravity.CENTER);tile.addView(i);TextView v=txt(value,19,WHITE,true);v.setGravity(Gravity.CENTER);tile.addView(v);TextView l=txt(label,10,0xFFD7E7FF,false);l.setGravity(Gravity.CENTER);tile.addView(l);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.setMargins(dp(3),0,dp(3),0);parent.addView(tile,p);}
    private void refreshProfilePhoto(){if(profilePhotoView==null)return;try{if(!profileImageUri.isEmpty()){profilePhotoView.setImageURI(Uri.parse(profileImageUri));if(profilePhotoView.getDrawable()!=null)return;}}catch(Exception ignored){}TextView emoji=txt(profileAvatar.isEmpty()?"🦉":profileAvatar,58,WHITE,true);emoji.setGravity(Gravity.CENTER);emoji.setBackground(shape(0xFF0B347F,100));emoji.measure(View.MeasureSpec.makeMeasureSpec(dp(116),View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(dp(116),View.MeasureSpec.EXACTLY));emoji.layout(0,0,dp(116),dp(116));android.graphics.Bitmap bitmap=android.graphics.Bitmap.createBitmap(dp(116),dp(116),android.graphics.Bitmap.Config.ARGB_8888);emoji.draw(new android.graphics.Canvas(bitmap));profilePhotoView.setImageBitmap(bitmap);profilePhotoView.setContentDescription("Profile avatar "+profileAvatar);}
    private void showAvatarOptions(){String[] choices={"Choose photo from gallery","Choose one of 20 avatars"};new AlertDialog.Builder(this).setTitle("Profile picture").setItems(choices,(d,w)->{if(w==0){Intent pick=new Intent(Intent.ACTION_OPEN_DOCUMENT);pick.addCategory(Intent.CATEGORY_OPENABLE);pick.setType("image/*");pick.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(pick,79);}else showAvatarPicker();}).show();}
    private void showAvatarPicker(){String[] labels=new String[profileAvatars.length];for(int i=0;i<labels.length;i++)labels[i]=profileAvatars[i]+"  Avatar "+(i+1);new AlertDialog.Builder(this).setTitle("Choose your avatar").setItems(labels,(d,w)->{profileAvatar=profileAvatars[w];profileImageUri="";prefs.edit().putString("profile_avatar",profileAvatar).remove("profile_image_uri").apply();showProfile();}).show();}
    private void showSettings(){base("Settings",-1);addButton("👤  My Profile",this::showProfile);addButton("🌐  App language: English",()->message("The current app interface is English."));addButton("🎯  Daily goal: 5 lessons",()->new AlertDialog.Builder(this).setTitle("Daily goal").setItems(new String[]{"3 lessons","5 lessons","10 lessons"},(d,w)->message("Daily goal preference selected." )).show());addButton("🔔  Notifications",()->message("Notifications can be enabled after notification scheduling is added."));addButton("ℹ️  About LINGEXA",()->message("LINGEXA — Learn. Practice. Speak.\nOffline-first learning prototype."));addButton("Reset local progress",()->new AlertDialog.Builder(this).setTitle("Reset progress?").setMessage("This clears XP, gems, hearts and saved learning progress on this device.").setNegativeButton("Cancel",null).setPositiveButton("Reset",(d,w)->{prefs.edit().clear().apply();loadState();showHome();}).show());}
    private void message(String s){new AlertDialog.Builder(this).setMessage(s).setPositiveButton("OK",null).show();}
    @Override protected void onDestroy(){if(tts!=null)tts.shutdown();super.onDestroy();}
}
