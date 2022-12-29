package com.tanhua.model.vo;

import com.tanhua.model.domain.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVo {
    /**
     * id
     */
    private String id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    public static AdminVo init(Admin admin) {
        AdminVo vo = new AdminVo();
        vo.setAvatar(admin.getAvatar());
        vo.setUsername(admin.getUsername());
        vo.setId(admin.getId().toString());
        return vo;
    }

}