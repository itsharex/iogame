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
package com.iohao.game.common.kit.concurrent.executor;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * 简单的线程执行器管理域
 * <pre>
 *     执行器的数量与 Runtime.getRuntime().availableProcessors() 相同。
 *
 *     SimpleThreadExecutorRegion - 简单的线程执行器管理域
 *     该执行器与 {@link UserThreadExecutorRegion} 类似。
 *     可通过 index 来得到对应的 ThreadExecutor 执行业务，从而避免并发问题。
 *     如果业务是计算密集型的，又不想占用 {@link UserThreadExecutorRegion} 线程资源时，可使用该执行器。
 * </pre>
 *
 * @author 渔民小镇
 * @date 2023-12-01
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
final class SimpleThreadExecutorRegion extends AbstractThreadExecutorRegion {

    SimpleThreadExecutorRegion(String threadName) {
        super(threadName, Runtime.getRuntime().availableProcessors());
    }

    SimpleThreadExecutorRegion() {
        this("Simple");
    }

    @Override
    public ThreadExecutor getThreadExecutor(long index) {
        var i = (int) (index % this.executorLength);
        return this.threadExecutors[i];
    }
}
