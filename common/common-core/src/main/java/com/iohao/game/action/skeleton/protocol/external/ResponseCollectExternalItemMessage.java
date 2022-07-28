/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2022 double joker （262610965@qq.com） . All Rights Reserved.
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
package com.iohao.game.action.skeleton.protocol.external;

import com.iohao.game.action.skeleton.core.exception.MsgException;
import com.iohao.game.action.skeleton.core.exception.MsgExceptionInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 渔民小镇
 * @date 2022-07-27
 */
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ResponseCollectExternalItemMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 8687159017486669115L;

    /** 由框架赋值 */
    String logicServerId;
    /** 响应码: 0:成功, 其他表示有错误 */
    int responseStatus;
    /** 错误消息 */
    String errorMsg;
    /** 响应数据，通常是（游戏对外服提供） */
    Serializable data;

    public boolean success() {
        return responseStatus == 0;
    }

    public void setError(MsgExceptionInfo msgExceptionInfo) {
        this.responseStatus = msgExceptionInfo.getCode();
        this.errorMsg = msgExceptionInfo.getMsg();
    }

    public void setError(MsgException msgException) {
        this.responseStatus = msgException.getMsgCode();
        this.errorMsg = msgException.getMessage();
    }
}
