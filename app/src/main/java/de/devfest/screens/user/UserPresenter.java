package de.devfest.screens.user;

import javax.inject.Inject;

import dagger.Lazy;
import de.devfest.data.UserManager;
import de.devfest.mvpbase.AuthPresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserPresenter extends AuthPresenter<UserView> {

    @Inject
    public UserPresenter(Lazy<UserManager> userManager) {
        super(userManager);
    }


    @Override
    public void attachView(UserView mvpView) {
        super.attachView(mvpView);
        untilDetach(userManager.get().loggedInState()
                .switchMap(state -> userManager.get().getCurrentUser().toObservable().onErrorReturn(error -> null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            if (user != null) {
                                getView().showUser(user);
                            } else {
                                getView().showLogin();
                            }
                        }
                ));
    }

    public void requestLogin() {
        getView().startLogin();
    }

    public void requestLogout() {
        userManager.get().logout();
    }
}
