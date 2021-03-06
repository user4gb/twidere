/*
 *				Twidere - Twitter client for Android
 * 
 * Copyright (C) 2012 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.fragment;

import static org.mariotaku.twidere.util.Utils.getAccountScreenName;

import java.util.List;

import org.mariotaku.twidere.loader.UserFollowersLoader;
import org.mariotaku.twidere.model.ParcelableUser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;

public class UserFollowersFragment extends BaseUsersListFragment {

	private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (BROADCAST_MULTI_BLOCKSTATE_CHANGED.equals(action)) {
				final long[] ids = intent.getLongArrayExtra(INTENT_KEY_USER_IDS);
				final long account_id = intent.getLongExtra(INTENT_KEY_ACCOUNT_ID, -1);
				final String screen_name = getAccountScreenName(getActivity(), account_id);
				final Bundle args = getArguments();
				if (ids == null || args == null) return;
				if (account_id > 0 && args.getLong(INTENT_KEY_USER_ID, -1) == account_id || screen_name != null
						&& screen_name.equalsIgnoreCase(args.getString(INTENT_KEY_SCREEN_NAME))) {
					for (final long id : ids) {
						removeUser(id);
					}
				}
			}
		}

	};

	@Override
	public Loader<List<ParcelableUser>> newLoaderInstance(final Context context, final Bundle args) {
		if (args == null) return null;
		final long account_id = args.getLong(INTENT_KEY_ACCOUNT_ID, -1);
		final long max_id = args.getLong(INTENT_KEY_MAX_ID, -1);
		final long user_id = args.getLong(INTENT_KEY_USER_ID, -1);
		final String screen_name = args.getString(INTENT_KEY_SCREEN_NAME);
		return new UserFollowersLoader(context, account_id, user_id, screen_name, max_id, getData());
	}

	@Override
	public void onStart() {
		super.onStart();
		final IntentFilter filter = new IntentFilter(BROADCAST_MULTI_BLOCKSTATE_CHANGED);
		registerReceiver(mStateReceiver, filter);

	}

	@Override
	public void onStop() {
		unregisterReceiver(mStateReceiver);
		super.onStop();
	}

}
