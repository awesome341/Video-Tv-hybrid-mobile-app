package com.hitherejoe.vineyard;


import com.hitherejoe.vineyard.data.DataManager;
import com.hitherejoe.vineyard.data.local.PreferencesHelper;
import com.hitherejoe.vineyard.data.model.Authentication;
import com.hitherejoe.vineyard.data.remote.VineyardService;
import com.hitherejoe.vineyard.util.DefaultConfig;
import com.hitherejoe.vineyard.util.MockModelsUtil;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class DataManagerTest {

    private DataManager mDataManager;
    private VineyardService mMockVineyardService;


    @Before
    public void setUp() {
        mMockVineyardService = mock(VineyardService.class);
        mDataManager = new DataManager(mMockVineyardService,
                mock(Bus.class),
                new PreferencesHelper(RuntimeEnvironment.application),
                Schedulers.immediate());
    }

    @Test
    public void shouldGetAccessToken() throws Exception {
        Authentication mockAuthentication = MockModelsUtil.createMockSuccessAuthentication();
        when(mMockVineyardService.getAccessToken(anyString(), anyString()))
                .thenReturn(Observable.just(mockAuthentication));

        TestSubscriber<Authentication> result = new TestSubscriber<>();
        mDataManager.getAccessToken("", "").subscribe(result);
        result.assertNoErrors();
        result.assertValue(mockAuthentication);

        assertEquals(mockAuthentication.data.key, mDataManager.getPreferencesHelper().getAccessToken());
    }

    @Test
    public void shouldFailGetAccessToken() throws Exception {
        Authentication mockAuthentication = MockModelsUtil.createMockErrorAuthentication();
        when(mMockVineyardService.getAccessToken(anyString(), anyString()))
                .thenReturn(Observable.just(mockAuthentication));

        TestSubscriber<Authentication> result = new TestSubscriber<>();
        mDataManager.getAccessToken("", "").subscribe(result);
        result.assertNoErrors();
        result.assertValue(mockAuthentication);

        assertEquals(mDataManager.getPreferencesHelper().getAccessToken(), null);
    }

}