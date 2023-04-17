/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2023 double joker （262610965@qq.com） . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 */
package com.iohao.game.action.skeleton.core.doc;

import com.iohao.game.action.skeleton.core.ActionCommand;
import com.iohao.game.action.skeleton.core.flow.parser.MethodParsers;
import com.iohao.game.action.skeleton.protocol.wrapper.ByteValueList;
import com.iohao.game.common.kit.StrKit;
import lombok.Getter;

import java.util.*;

/**
 * @author 渔民小镇
 * @date 2022-01-29
 */
@Getter
class DocInfo {
    String actionSimpleName;
    String classComment;
    ActionSendDocsRegion actionSendDocsRegion;

    final List<Map<String, String>> subBehaviorList = new ArrayList<>();

    public void setHead(ActionCommand subBehavior) {
        ActionCommandDoc actionCommandDoc = subBehavior.getActionCommandDoc();
        this.actionSimpleName = subBehavior.getActionControllerClazz().getSimpleName();
        this.classComment = actionCommandDoc.getClassComment();
    }

    void add(ActionCommand subBehavior) {
        Map<String, String> paramMap = new HashMap<>(16);
        subBehaviorList.add(paramMap);

        ActionCommandDoc actionCommandDoc = subBehavior.getActionCommandDoc();

        int cmd = subBehavior.getCmdInfo().getCmd();
        int subCmd = subBehavior.getCmdInfo().getSubCmd();
        var actionMethodReturnInfo = subBehavior.getActionMethodReturnInfo();

        paramMap.put("cmd", String.valueOf(cmd));
        paramMap.put("subCmd", String.valueOf(subCmd));
        paramMap.put("actionSimpleName", subBehavior.getActionControllerClazz().getSimpleName());
        paramMap.put("methodName", subBehavior.getActionMethodName());
        paramMap.put("methodComment", actionCommandDoc.getComment());
        paramMap.put("methodParam", "");
        paramMap.put("returnTypeClazz", returnToString(actionMethodReturnInfo));
        paramMap.put("lineNumber", String.valueOf(actionCommandDoc.getLineNumber()));

        // 方法参数
        Arrays.stream(subBehavior.getParamInfos())
                .filter(paramInfo -> !paramInfo.isExtension())
                .map(this::paramInfoToString)
                .forEach(methodParam -> paramMap.put("methodParam", methodParam));

        if (subBehavior.isThrowException()) {
            paramMap.put("error", "");
        }
    }

    private String paramInfoToString(ActionCommand.ParamInfo paramInfo) {
        Class<?> actualClazz = paramInfo.getActualClazz();
        boolean isCustomList = paramInfo.isList() && !MethodParsers.me().containsKey(actualClazz);
        return paramResultInfoToString(actualClazz, isCustomList);
    }

    private String returnToString(ActionCommand.ActionMethodReturnInfo actionMethodReturnInfo) {
        Class<?> actualClazz = actionMethodReturnInfo.getActualClazz();
        boolean isCustomList = actionMethodReturnInfo.isList() && !MethodParsers.me().containsKey(actualClazz);
        return paramResultInfoToString(actualClazz, isCustomList);
    }

    private String paramResultInfoToString(Class<?> actualClazz, boolean isCustomList) {
        if (isCustomList) {
            /*
             * 因为是生成对接文档，所以不能使用 List<xxx> 来表示，而是使用 ByteValueList<xxx> 来表示。
             * 因为 ByteValueList 是一个类似 IntValueList、LongValueList 这样的包装类
             */
            String simpleName = ByteValueList.class.getSimpleName();
            String simpleNameActualClazz = actualClazz.getSimpleName();
            return String.format("%s<%s>", simpleName, simpleNameActualClazz);
        }

        return actualClazz.getSimpleName();
    }

    String render() {
        if (this.subBehaviorList.isEmpty()) {
            return "";
        }

        String separator = System.getProperty("line.separator");

        List<String> lineList = new ArrayList<>();

        String templateHead = "==================== {} {} ====================";
        lineList.add(StrKit.format(templateHead, this.actionSimpleName, this.classComment));

        String subActionCommandTemplate =
                "路由: {cmd} - {subCmd}  --- 【{methodComment}】 --- 【{actionSimpleName}:{lineNumber}】【{methodName}】";

        for (Map<String, String> paramMap : subBehaviorList) {

            int cmd = Integer.parseInt(paramMap.get("cmd"));
            int subCmd = Integer.parseInt(paramMap.get("subCmd"));

            ActionSendDoc actionSendDoc = this.actionSendDocsRegion.getActionSendDoc(cmd, subCmd);

            String format = StrKit.format(subActionCommandTemplate, paramMap);
            lineList.add(format);

            if (paramMap.containsKey("error")) {
                lineList.add("    触发异常: (方法有可能会触发异常)");
            }

            // 方法参数
            if (StrKit.isNotEmpty(paramMap.get("methodParam"))) {
                format = StrKit.format("    方法参数: {methodParam}", paramMap);
                lineList.add(format);
            }

            // 方法返回值
            if (StrKit.isNotEmpty(paramMap.get("returnTypeClazz"))) {
                format = StrKit.format("    方法返回值: {returnTypeClazz}", paramMap);
                lineList.add(format);
            }

            // 广播推送
            if (Objects.nonNull(actionSendDoc)) {
                Class<?> dataClass = actionSendDoc.getDataClass();
                format = StrKit.format("    广播推送: {}", dataClass.getName());
                lineList.add(format);
            }

            lineList.add(" ");
        }

        lineList.add(separator);

        return String.join(separator, lineList);
    }
}
