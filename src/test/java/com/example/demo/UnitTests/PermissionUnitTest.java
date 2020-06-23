package com.example.demo.UnitTests;

import com.example.demo.BusinessLayer.Entities.Permission;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class PermissionUnitTest {
    private Permission permission;

    @Test
    public void settersGettersTest() {
        String text = "SYS_ADMIN";
        permission = new Permission(text);
        Assert.assertEquals(text, permission.getPermissionName());

        text = "GUEST";
        permission.setPermissionName(text);
        int id = 1;
        permission.setPermissionId(id);

        Assert.assertEquals(text, permission.getPermissionName());
        Assert.assertEquals(id, permission.getPermissionId());
    }
}
