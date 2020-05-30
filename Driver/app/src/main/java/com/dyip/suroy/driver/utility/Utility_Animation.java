package com.dyip.suroy.driver.utility;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.dyip.suroy.driver.Constants;
import com.dyip.suroy.driver.FailedAuthActivity;
import com.dyip.suroy.driver.MapsActivity;
import com.dyip.suroy.driver.R;

/**
 * Utility class for handling animations in the GUI
 */
public class Utility_Animation {

    public static void animateStartup(final Activity activity) {

        ImageView jeep = activity.findViewById(R.id.jeepStartup);

        ObjectAnimator tx = ObjectAnimator.ofFloat(jeep, "translationX", 0);
        tx.setDuration(1000);
        tx.setStartDelay(300);

        ObjectAnimator rot = ObjectAnimator.ofFloat(jeep, "rotation", 0);
        rot.setDuration(300);
        rot.setStartDelay(1000);
        rot.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (Constants.fireUser == null) {
                    View login = activity.findViewById(R.id.sign_in_button);
                    login.setVisibility(View.VISIBLE);
                    login.animate().alpha(1).setDuration(1000).start();
                } else {
                    Utility_Auth.validateLoginFirebase(activity);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(tx, rot);
        set.start();

    }

    public static void animateLoginSuccess(final Activity activity) {
        final View login = activity.findViewById(R.id.sign_in_button);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(login, "alpha", 0);
        alpha.setDuration(750);
        alpha.setStartDelay(300);
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                login.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        final ImageView jeep = activity.findViewById(R.id.jeepStartup);

        ObjectAnimator rot = ObjectAnimator.ofFloat(jeep, "rotation", 20);
        rot.setStartDelay(0);
        rot.setDuration(300);

        int offset = (int) activity.getResources().getDimension(R.dimen.jeep_offset);
        ObjectAnimator tx = ObjectAnimator.ofFloat(jeep, "translationX", -offset);
        tx.setDuration(1000);
        tx.setStartDelay(100);
        tx.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (Constants.validatedUID) {
//                    TODO: Proceed to maps activity
                    Utility.log("Animation", "Login success!");
                    Intent intent = new Intent(activity, MapsActivity.class);
                    activity.startActivity(intent);
                } else {
//                    TODO: Proceed to authentication failed page
                    Utility.log("Animation", "Login failed!");
                    Intent intent = new Intent(activity, FailedAuthActivity.class);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(alpha).before(rot);
        set.playTogether(rot, tx);
        set.start();

    }

}
