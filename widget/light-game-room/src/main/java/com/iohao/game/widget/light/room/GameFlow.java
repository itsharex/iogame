/*
 * ioGame
 * Copyright (C) 2021 - 2023  渔民小镇 （262610965@qq.com、luoyizhu@gmail.com） . All Rights Reserved.
 * # iohao.com . 渔民小镇
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.iohao.game.widget.light.room;

import com.iohao.game.action.skeleton.core.exception.MsgException;
import com.iohao.game.widget.light.room.flow.*;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * 游戏流程
 * <pre>
 *     游戏规则
 *     创建玩家
 *
 *     房间创建
 *
 *     进入房间
 *     游戏开始
 * </pre>
 *
 * @author 渔民小镇
 * @date 2022-03-31
 */
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameFlow {
    /** 游戏规则 */
    RoomRuleInfoCustom roomRuleInfoCustom;
    /** 游戏开始 */
    RoomGameStartCustom roomGameStartCustom;
    /** 创建玩家 */
    RoomPlayerCreateCustom roomPlayerCreateCustom;
    /** 房间创建 */
    RoomCreateCustom roomCreateCustom;
    /** 进入房间 */
    RoomEnterCustom roomEnterCustom;

    /**
     * 游戏开始
     *
     * @param abstractRoom room
     * @return true 游戏开始
     */
    public boolean startGame(AbstractRoom abstractRoom) {
        boolean startBefore = this.roomGameStartCustom.startBefore(abstractRoom);

        if (startBefore) {
            this.roomGameStartCustom.startAfter(abstractRoom);
        }

        return startBefore;
    }

    /**
     * 创建房间, 子类只需要关心 ruleInfo (房间配置, 规则信息)
     * <p>
     * 根据 创建游戏规则
     *
     * @param createRoomInfo 创建房间信息
     * @param <T>            AbstractRoom
     * @return 房间
     */
    public <T extends AbstractRoom> T createRoom(CreateRoomInfo createRoomInfo) {
        return this.roomCreateCustom.createRoom(createRoomInfo);
    }

    /**
     * 构建房间内的玩家
     *
     * @param <T> AbstractPlayer
     * @return 玩家
     */
    public <T extends AbstractPlayer> T createPlayer() {
        return this.roomPlayerCreateCustom.createPlayer();
    }

    /**
     * 进入房间
     *
     * @param userId        玩家 id
     * @param abstractRoom  玩家所在房间
     * @param roomEnterInfo 进入房间请求信息
     * @return enter Response
     */
    public RoomEnterInfo enterRoom(long userId, AbstractRoom abstractRoom, RoomEnterInfo roomEnterInfo) {
        return this.roomEnterCustom.enterRoom(userId, abstractRoom, roomEnterInfo);
    }

    /**
     * 获取子游戏规则信息
     *
     * @param ruleInfoJson 游戏规则 - json
     * @return 规则信息
     * @throws MsgException e
     */
    public RuleInfo getRuleInfo(String ruleInfoJson) throws MsgException {
        return this.roomRuleInfoCustom.getRuleInfo(ruleInfoJson);
    }
}
