package bigcash.poker.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import bigcash.poker.constants.Constant;
import bigcash.poker.gwt.PokerGame;
import bigcash.poker.utils.PokerUtils;

public class HtmlLauncher extends GwtApplication {
        private PokerGame pokerGame;
        @Override
        public GwtApplicationConfiguration getConfig () {
                GwtApplicationConfiguration configuration=new GwtApplicationConfiguration(true);
                configuration.padVertical=0;
                configuration.padHorizontal=0;
                configuration.fullscreenOrientation= GwtGraphics.OrientationLockType.LANDSCAPE;
                Constant.deviceId= TimeUtils.millis()+"";
                Constant.androidId= TimeUtils.nanoTime()+"";
                pokerGame.setAppConfig(configuration);
                return configuration;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return pokerGame=new PokerGame();
        }

        @Override
        public void onModuleLoad() {
                super.onModuleLoad();
                Window.enableScrolling(true);
        }
        public Preloader.PreloaderCallback getPreloaderCallback () {
                return getPreloaderPanel();
        }
        private Preloader.PreloaderCallback getPreloaderPanel(){
                final Panel preloaderPanel = new VerticalPanel();
                preloaderPanel.setStyleName("gdx-preloader");
                final Image logo = new Image("img/process.gif");
                logo.setStyleName("logo");
                preloaderPanel.add(logo);
                final Panel meterPanel = new SimplePanel();
                final InlineHTML meter = new InlineHTML();
                final Style meterStyle = meter.getElement().getStyle();
                meterStyle.setWidth(0, Style.Unit.PCT);
                adjustMeterPanel(meterPanel, meterStyle);
                meterPanel.add(meter);
                preloaderPanel.add(meterPanel);
                getRootPanel().add(preloaderPanel);
                return new Preloader.PreloaderCallback() {
                        @Override
                        public void error (String file) {
                                System.out.println("error: " + file);
                        }

                        @Override
                        public void update (Preloader.PreloaderState state) {
                                meterStyle.setWidth(100f * state.getProgress(), Style.Unit.PCT);
                        }

                };
        }

}