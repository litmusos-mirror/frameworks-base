/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs;

import com.android.systemui.qs.dagger.QSScope;
import com.android.systemui.util.ViewController;

import javax.inject.Inject;

/**
 * Controller for {@link QuickStatusBarHeader}.
 */
@QSScope
class QuickStatusBarHeaderController extends ViewController<QuickStatusBarHeader> {

    private final QuickQSPanelController mQuickQSPanelController;
    private boolean mListening;

    @Inject
    QuickStatusBarHeaderController(QuickStatusBarHeader view,
            QuickQSPanelController quickQSPanelController
    ) {
        super(view);
        mQuickQSPanelController = quickQSPanelController;
    }

    @Override
    protected void onViewAttached() {
        mPrivacyIconsController.onParentVisible();
        mPrivacyIconsController.setChipVisibilityListener(this);
        mIconContainer.addIgnoredSlot(
                getResources().getString(com.android.internal.R.string.status_bar_managed_profile));
        mIconContainer.setShouldRestrictIcons(false);
        mStatusBarIconController.addIconGroup(mIconManager);

        mView.setIsSingleCarrier(mQSCarrierGroupController.isSingleCarrier());
        mQSCarrierGroupController
                .setOnSingleCarrierChangedListener(mView::setIsSingleCarrier);

        List<String> rssiIgnoredSlots = List.of(
                getResources().getString(com.android.internal.R.string.status_bar_mobile)
        );

        mView.onAttach(mIconManager, mQSExpansionPathInterpolator, rssiIgnoredSlots,
                mInsetsProvider, mFeatureFlags.isEnabled(Flags.COMBINED_QS_HEADERS));

        mDemoModeController.addCallback(mDemoModeReceiver);

        mVariableDateViewControllerDateView.init();
        mVariableDateViewControllerClockDateView.init();
    }

    @Override
    protected void onViewDetached() {
        setListening(false);
    }

    public void setListening(boolean listening) {
        if (listening == mListening) {
            return;
        }
        mListening = listening;

        mQuickQSPanelController.setListening(listening);

        if (mQuickQSPanelController.switchTileLayout(false)) {
            mView.updateResources();
        }
    }

    public void setContentMargins(int marginStart, int marginEnd) {
        mQuickQSPanelController.setContentMargins(marginStart, marginEnd);
    }
}
