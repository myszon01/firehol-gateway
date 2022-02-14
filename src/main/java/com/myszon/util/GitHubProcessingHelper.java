package com.myszon.util;

import com.myszon.api.responses.Blob;
import com.myszon.api.responses.Tree;
import com.myszon.api.responses.TreeType;
import com.myszon.model.IpAddress;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class GitHubProcessingHelper {

    public static boolean shouldIgnoreBlobOrTree(Tree tree) {
        if (tree.getType() == TreeType.TREE) return true;
        int extIdx = tree.getPath().lastIndexOf('.');
        return tree.getPath().substring(extIdx + 1).equals("ipset");
    }

    public static List<IpAddress> getIpAddressFromBlob(Blob blob) {
        byte[] bytes = Base64.getMimeDecoder().decode(blob.getContent());
        String page = new String(bytes, StandardCharsets.UTF_8);
        String[] lines = page.split("\n");

        List<IpAddress> list = new ArrayList<>();
        for (String line : lines) {
            if (line.charAt(0) == '#') continue;

            IpAddress ip1 = new IpAddress();
            ip1.setIpAddress(line);
            list.add(ip1);
        }

        return list;
    }

    //    TODO use it for unit test
    String test = "IwojIGFsaWVudmF1bHRfcmVwdXRhdGlvbgojCiMgaXB2NCBoYXNoOmlwIGlw\n" +
            "c2V0CiMKIyBbQWxpZW5WYXVsdC5jb21dIChodHRwczovL3d3dy5hbGllbnZh\n" +
            "dWx0LmNvbS8pIElQIAojIHJlcHV0YXRpb24gZGF0YWJhc2UKIwojIE1haW50\n" +
            "YWluZXIgICAgICA6IEFsaWVuIFZhdWx0CiMgTWFpbnRhaW5lciBVUkwgIDog\n" +
            "aHR0cHM6Ly93d3cuYWxpZW52YXVsdC5jb20vCiMgTGlzdCBzb3VyY2UgVVJM\n" +
            "IDogaHR0cHM6Ly9yZXB1dGF0aW9uLmFsaWVudmF1bHQuY29tL3JlcHV0YXRp\n" +
            "b24uZ2VuZXJpYwojIFNvdXJjZSBGaWxlIERhdGU6IEZyaSBOb3YgMTIgMTQ6\n" +
            "MTA6NTAgVVRDIDIwMjEKIwojIENhdGVnb3J5ICAgICAgICA6IHJlcHV0YXRp\n" +
            "b24KIyBWZXJzaW9uICAgICAgICAgOiA0MjM2CiMKIyBUaGlzIEZpbGUgRGF0\n" +
            "ZSAgOiBGcmkgTm92IDEyIDE1OjUyOjI1IFVUQyAyMDIxCiMgVXBkYXRlIEZy\n" +
            "ZXF1ZW5jeTogNiBob3VycyAKIyBBZ2dyZWdhdGlvbiAgICAgOiBub25lCiMg\n" +
            "RW50cmllcyAgICAgICAgIDogNjA5IHVuaXF1ZSBJUHMKIwojIEZ1bGwgbGlz\n" +
            "dCBhbmFseXNpcywgaW5jbHVkaW5nIGdlb2xvY2F0aW9uIG1hcCwgaGlzdG9y\n" +
            "eSwKIyByZXRlbnRpb24gcG9saWN5LCBvdmVybGFwcyB3aXRoIG90aGVyIGxp\n" +
            "c3RzLCBldGMuCiMgYXZhaWxhYmxlIGF0OgojCiMgIGh0dHA6Ly9pcGxpc3Rz\n" +
            "LmZpcmVob2wub3JnLz9pcHNldD1hbGllbnZhdWx0X3JlcHV0YXRpb24KIwoj\n" +
            "IEdlbmVyYXRlZCBieSBGaXJlSE9MJ3MgdXBkYXRlLWlwc2V0cy5zaAojIFBy\n" +
            "b2Nlc3NlZCB3aXRoIEZpcmVIT0wncyBpcHJhbmdlCiMKMS4zNC41OC4xMTAK\n" +
            "MS4zNC4yMjYuNTAKMS4xNjEuMjE5Ljg2CjEuMTcxLjEwMy4xOTIKMS4xNzMu\n" +
            "MjQyLjE2MQoxLjI0Ni4yMjIuMjAKMS4yNDYuMjIyLjEzNAoxLjI0Ni4yMjIu\n" +
            "MjM0CjEuMjQ2LjIyMy4xOTEKMi4xMDYuMTU2LjUzCjMuMjEuNzQuMzEKNC43\n" +
            "MS4zNy40NQo0LjcxLjM3LjQ2CjE0LjM0LjE1Ny4xMDEKMTQuNDIuMTQ1LjE3\n" +
            "MgoxNC4xMTEuMjIwLjEzNAoxNC4yNDEuMjQ0LjI1MAoxOC4xMTcuNjkuMTM1\n" +
            "CjE4LjE4OC4xNDguODAKMjMuMjQ3LjEwOC40NAoyNC4xODguMTAwLjg1CjI3\n" +
            "LjIxLjE0Ny4yMDkKMjcuMzUuMTU0Ljc1CjI3LjM4LjYxLjc1CjI3LjM4LjYx\n" +
            "LjEyMAoyNy40MS4zNi4yMzkKMjcuNDMuMTE5LjE0NAoyNy40My4xNzguMTEy\n" +
            "CjI3LjQ3LjExNi4yNDkKMjcuMTU4Ljc5LjEyOQoyNy4xNTkuOTIuMTgxCjI3\n" +
            "LjE5NC44OS4xODkKMjcuMTk0LjEyMi4yMwoyNy4xOTcuMjQuMjIzCjI3LjE5\n" +
            "OS4yMzcuMTYyCjI3LjIwMy4yMzMuMTMyCjI3LjIwNy4xOTUuMTI2CjI3LjIx\n" +
            "NS41My4xMTEKMjcuMjE1LjEwOS4xOTYKMjcuMjE1LjExNC4yMjMKMjcuMjE1\n" +
            "LjEyMi4xNjAKMjcuMjE3LjE2My40MAoyNy4yMTcuMjQzLjE2MwozNi4yMjgu\n" +
            "NTAuNzcKMzYuMjMxLjM1LjE4NQozNy4wLjEwLjMxCjM5LjY2LjczLjUwCjM5\n" +
            "Ljc0LjE3Ny4xNjcKMzkuODEuNzEuNzgKNDEuODYuNS4yMzIKNDEuODYuMTgu\n" +
            "MzQKNDEuODYuMTguMTY1CjQxLjg2LjE5LjE0Ngo0Mi41MS41NS4xNTcKNDIu\n" +
            "MTE1LjMzLjk4CjQyLjIyOC4xOTMuNjcKNDIuMjMxLjE3MS4yNDUKNDMuMjUx\n" +
            "Ljk5LjYKNDQuMTkyLjI0NC4xNzgKNDUuMTQ2LjE2NC4xMTAKNDUuMjI5LjU0\n" +
            "LjU1CjQ1LjIyOS41NC44Mwo0NS4yMjkuNTQuMTQzCjQ1LjIyOS41NC4xOTMK\n" +
            "NDUuMjI5LjU0LjE5OQo0NS4yMjkuNTQuMjEyCjQ1LjIyOS41NS41Nwo0NS4y\n" +
            "MjkuNTUuNjkKNDUuMjI5LjU1LjExMgo0NS4yNDguMTkyLjQ4CjQ2LjQuMTIz\n" +
            "LjE1CjQ2LjEwMS4xMy45NAo0Ni4xODMuMjE4LjE1MQo0OS43Ni42MC4xMzIK\n" +
            "NDkuODkuNjIuMjUyCjQ5Ljg5LjkwLjE3Mwo0OS44OS45My4yMQo0OS44OS45\n" +
            "NS4xNTkKNDkuMTQzLjMyLjYKNDkuMTU4LjE5Ni4xOAo0OS4yMTMuMTgzLjIx\n" +
            "OQo0OS4yMTMuMTg3LjI0Ngo1MS4xNS4yMjguMTE3CjUxLjE1LjI0Ni4xMDQK\n" +
            "NTEuMTU4LjY0LjExMwo1MS4xNTguMTAyLjEzMgo1MS4xNTguMTA4LjIzNwo1\n" +
            "MS4xNTguMTE3LjE2NAo1MS4xNTguMTI1LjIyNgo1MS4yMTEuMjQuMTYwCjUx\n" +
            "LjIxMS4xMTIuNzkKNTEuMjExLjExNy4xMDkKNTguNTguNDEuMTA2CjU4Ljk5\n" +
            "Ljk5LjM0CjU4LjIxOS4yMzIuMTQwCjU4LjI0OC4xNDcuNjQKNTguMjQ4LjE5\n" +
            "My4zCjU4LjI0OC4xOTMuNTAKNTguMjQ4LjE5My44OAo1OC4yNDguMTkzLjk3\n" +
            "CjU4LjI0OC4xOTMuMTA1CjU4LjI0OC4xOTMuMTMyCjU4LjI0OC4xOTMuMTQx\n" +
            "CjU4LjI0OC4xOTMuMjMyCjU4LjI0OC4xOTMuMjQ2CjU4LjI0OS4xMi45NQo1\n" +
            "OC4yNDkuODcuNzgKNTguMjQ5LjExMC4xOTgKNTguMjUzLjEyLjkKNTkuNjMu\n" +
            "MjA0Ljc2CjU5LjYzLjIwNC4yNDUKNTkuNjMuMjA3LjY5CjU5LjEyNi45Ni41\n" +
            "CjU5LjEyNy4yMDkuODgKNTkuMTc1LjYzLjg5CjYwLjIxMi4xMDguMjQwCjYw\n" +
            "LjI0NC4xMzMuMTk1CjYxLjAuMTY4LjE0OQo2MS4xMjkuMTAxLjM4CjYxLjE1\n" +
            "Mi4xOTcuNTYKNjEuMjE5Ljk4LjQzCjYxLjIyNC4xNDcuMTc4CjYxLjI0Mi40\n" +
            "MC4xNAo2MS4yNDIuNDAuMTcKNjEuMjQyLjQwLjIwNAo2MS4yNDIuNDAuMjA2\n" +
            "CjYxLjI0Mi40MC4yMTIKNjEuMjQyLjQwLjIyNQo2MS4yNDIuNDAuMjI5CjYx\n" +
            "LjI0Mi40MC4yNDUKNjEuMjQyLjU0LjUKNjEuMjQyLjU0LjE2CjYxLjI0Mi41\n" +
            "NC40OAo2MS4yNDIuNTQuNDkKNjEuMjQyLjU0LjYwCjYxLjI0Mi41NC42Mgo2\n" +
            "MS4yNDIuNTQuMTI2CjYxLjI0Mi41NC4xMzcKNjEuMjQyLjU0LjE3MAo2MS4y\n" +
            "NDIuNTQuMTc1CjYxLjI0Mi41NC4yMDMKNjEuMjQyLjU0LjIxMQo2MS4yNDIu\n" +
            "NTQuMjE0CjYxLjI0Mi41NC4yMzkKNjEuMjQyLjU0LjI0Mgo2MS4yNDIuNTQu\n" +
            "MjQ5CjYxLjI0Mi41OC4xMgo2MS4yNDIuNTguMTQKNjEuMjQyLjU4LjQ3CjYx\n" +
            "LjI0Mi41OC42Ngo2MS4yNDIuNTguNjcKNjEuMjQyLjU4LjEwNAo2MS4yNDIu\n" +
            "NTguMTM1CjYxLjI0Mi41OC4xNzgKNjEuMjQyLjU4LjE5Nwo2MS4yNDIuNTgu\n" +
            "MjAwCjYxLjI0Mi41OC4yMzkKNjEuMjQyLjU4LjI0Ngo2Mi40LjE0LjE5OAo2\n" +
            "Mi4xNi40MS4yMTAKNjIuMTcxLjE1OS4yMDcKNjIuMjE5LjIyOS4xOTAKNjQu\n" +
            "MzkuMTA4Ljk0CjY1LjEwOC4xMS4xNjMKNjguMTgzLjEwNy42NAo2OS41NS41\n" +
            "NS4yMzAKNjkuMTc2Ljg5LjIyNgo3MC41MC4xNTIuMTMwCjcwLjUwLjE1NS4y\n" +
            "NTEKNzEuNjguMjI5LjI0Nwo3OC4xNDIuMTguNTYKNzguMTU0LjIxOS4xNjkK\n" +
            "NzguMTg2LjI0OC4yNDMKNzguMTg3LjE5Ni4zOAo3OC4xODguMjQwLjIzMAo3\n" +
            "OS4xNzAuMzAuMTQyCjgwLjE0LjIxNi4yMDQKODAuODIuNjUuMjQ3CjgwLjI0\n" +
            "My4xODEuODEKODAuMjQzLjE4MS4xMTkKODEuMjE0LjcyLjIxNQo4MS4yNTAu\n" +
            "MTY5LjI0OQo4My4xMzguNTMuMTI4Cjg0LjUzLjIyOS4xMgo4NS43MS4yNi4y\n" +
            "OAo4Ni4xNTcuMi4yMTEKODYuMTYxLjAuODYKODYuMTc1LjEwNS44MAo4Ni4x\n" +
            "ODEuNC4xNjkKODkuNDAuNzMuMTQKODkuMjA4LjEyMi4yMTMKOTEuMTA0LjMx\n" +
            "LjU2CjkxLjE4OC4yMTUuMTk4CjkxLjIzNC42Mi4yMzEKOTMuNTEuMjcuMTEz\n" +
            "CjkzLjY1LjIzLjIyMQo5My4xMTIuMTUyLjEzNAo5NC4xNTYuNTguMTcKOTUu\n" +
            "MTM3LjI0OC4xODIKMTAwLjI3LjQyLjI0MQoxMDAuMjcuNDIuMjQyCjEwMC4y\n" +
            "Ny40Mi4yNDMKMTAwLjI3LjQyLjI0NAoxMDEuMC4zMi4yMgoxMDEuMC40MS4y\n" +
            "NQoxMDEuMC41Ny42MAoxMDEuMC41Ny4xNTgKMTAxLjIyLjE0NC4xMzAKMTAx\n" +
            "LjY1LjEzMS4xNDQKMTAxLjE4MS4wLjE5OAoxMDEuMTgxLjE3LjExMgoxMDEu\n" +
            "MTgxLjE3LjEzNwoxMDEuMTgxLjI2LjE4NgoxMDEuMTgxLjI3LjgwCjEwMS4x\n" +
            "ODEuMzQuNjkKMTAxLjE4MS40MC4yMzMKMTAxLjE4MS42MC4xODEKMTAxLjE4\n" +
            "MS42OC43OQoxMDEuMTgxLjczLjkxCjEwMS4xODEuODIuMTAyCjEwMS4xODEu\n" +
            "OTguMTU4CjEwMS4xODEuMTAyLjEwOAoxMDEuMTgxLjEwNC4yNDEKMTAxLjE4\n" +
            "MS4xMTQuMTcyCjEwMS4xODEuMTMyLjM3CjEwMy4zNy4zLjU4CjEwMy40MC4x\n" +
            "NzIuMTczCjEwMy40MC4xNzIuMTg5CjEwMy40MC4xOTYuMgoxMDMuNDAuMTk2\n" +
            "LjI0CjEwMy40MC4xOTcuMjQKMTAzLjQwLjE5Ny4xNzUKMTAzLjkxLjE5LjIz\n" +
            "MQoxMDMuOTEuMjQ1LjQ4CjEwMy4xMDQuMTA2Ljk4CjEwMy4xMDQuMTA2LjIy\n" +
            "MwoxMDMuMTE5LjU1LjE1MQoxMDMuMTM2LjgyLjUwCjEwMy4xNzAuOTIuNQox\n" +
            "MDMuMTcwLjkyLjcKMTAzLjE3MC45Mi4xMAoxMDMuMTcwLjkyLjExCjEwMy4x\n" +
            "NzAuOTIuMjIKMTAzLjIwNi4yMS4xMDcKMTAzLjIxNS4yNDAuMQoxMDMuMjMx\n" +
            "LjE3Mi40MgoxMDQuMTMxLjE0LjE5MgoxMDQuMTMxLjgyLjQ1CjEwNC4yNDgu\n" +
            "MTYyLjMzCjEwNi4xMDQuMTE2Ljc5CjEwOS4xMTYuMjA0LjYzCjEwOS4xMjMu\n" +
            "MTE4LjM4CjExMC4yNS45NS4yNDEKMTEwLjM1LjIyNy4yMjIKMTEwLjg5LjEx\n" +
            "LjE0MwoxMTAuMjUxLjE5OC4yMwoxMTEuMzguMTA2LjQ4CjExMS45Mi43NS4x\n" +
            "ODgKMTExLjkyLjc1LjIxNwoxMTEuOTIuMTE2LjQ1CjExMS4xNjUuMzYuMTM0\n" +
            "CjExMS4xODUuMjI3LjEwOQoxMTEuMTg1LjIyOC4zNwoxMTEuMjAyLjE2Ny4y\n" +
            "MgoxMTEuMjAyLjE5MC42CjExMS4yNTIuMjEzLjI0NQoxMTIuNS4zNy4xNjAK\n" +
            "MTEyLjYuMjIxLjM3CjExMi4yNy4xMjQuMTExCjExMi4yNy4xMjQuMTMwCjEx\n" +
            "Mi4yNy4xMjQuMTQ1CjExMi4yNy4xMjQuMTU4CjExMi4zMC40LjczCjExMi4z\n" +
            "MC40LjExOAoxMTIuMzEuODcuOTgKMTEyLjMxLjIxMS4xMzUKMTEyLjg2LjI1\n" +
            "NS4xMDAKMTEyLjk0Ljk2LjExNAoxMTIuOTQuOTcuODUKMTEyLjk0Ljk3LjE2\n" +
            "NgoxMTIuOTQuOTguNgoxMTIuOTQuOTguNTcKMTEyLjk0Ljk4LjcxCjExMi45\n" +
            "NC45OC4xNTEKMTEyLjk0Ljk5Ljg0CjExMi45NC45OS44NgoxMTIuOTQuOTku\n" +
            "OTMKMTEyLjk0Ljk5LjEzOQoxMTIuOTQuMTAxLjE5MAoxMTIuOTQuMTAxLjIw\n" +
            "MwoxMTIuOTQuMTAxLjIzNQoxMTIuMTA1LjEwLjI1MQoxMTIuMjM1LjQ2LjEy\n" +
            "OAoxMTIuMjM3LjIuODAKMTEyLjIzOS4xMDMuNDMKMTEyLjIzOS4xMjAuMTUw\n" +
            "CjExMi4yNDguMTA5LjE1OQoxMTIuMjUwLjI0My43MgoxMTIuMjUxLjE4LjEy\n" +
            "CjExMi4yNTUuMTI2Ljg5CjExMy4xNzAuMTI4LjI0MgoxMTMuMjQ2LjEzMC4x\n" +
            "ODIKMTEzLjI1MS4yMzUuMTkKMTE0LjMzLjY0LjI0CjExNC4zMy4xOTAuMjQ2\n" +
            "CjExNC4zNC4xMzUuNTcKMTE0LjM1LjEzMS4xNjEKMTE0LjM1LjE3NS4yMzkK\n" +
            "MTE0LjM1LjE5NC4xOAoxMTQuMzYuMzQuNjMKMTE0LjQxLjIyNi45NgoxMTQu\n" +
            "MjM2LjUyLjEwMQoxMTQuMjM5LjUxLjc3CjExNC4yNDYuMzUuMTI5CjExNS41\n" +
            "MS4xMjIuMTQzCjExNS41OC4yMDIuOTIKMTE1LjYyLjU4LjEwCjExNS4xNjUu\n" +
            "MjIxLjk1CjExNi4yLjE3My4yMAoxMTYuNjguOTkuNzEKMTE2LjIxMS4xMDAu\n" +
            "MjYKMTE2LjIxMi4xNTYuMzEKMTE3LjM2LjE5OS4zOAoxMTcuODcuMTguMjcK\n" +
            "MTE3Ljk1LjEzNy45MwoxMTcuMjA0LjE0OS4yMDAKMTE3LjIwOC41MS41MQox\n" +
            "MTcuMjQwLjE0Mi4yMTIKMTE4Ljc5LjE0MC4xMzUKMTE4LjE2MS4yMTAuMjQ4\n" +
            "CjExOC4yNTAuMTU0LjI0MgoxMTkuMjkuMTE5LjE3NAoxMTkuMTIyLjExNC4x\n" +
            "MzgKMTE5LjE2NS4xMTEuMTQ3CjExOS4xNzkuMjM4LjEwMAoxMTkuMTkxLjE2\n" +
            "MC4yMjEKMTE5LjE5MS4yMTcuMTU1CjExOS4yMjQuOTEuMjMzCjEyMC4xLjE0\n" +
            "MC4yNQoxMjAuODUuOTIuNDAKMTIwLjg1LjkzLjE3NAoxMjAuODUuOTQuMTgy\n" +
            "CjEyMC44NS45Ny43MQoxMjAuODUuMTEyLjEyNAoxMjAuODUuMTEyLjEyOAox\n" +
            "MjAuODUuMTEyLjEzMwoxMjAuODUuMTEzLjQyCjEyMC44NS4xMTMuNTUKMTIw\n" +
            "Ljg1LjExMy4xMjAKMTIwLjg1LjExMy4yNTMKMTIwLjg1LjExNC42NAoxMjAu\n" +
            "ODUuMTE0LjE0NgoxMjAuODUuMTE0LjE2NAoxMjAuODUuMTE0LjE4OAoxMjAu\n" +
            "ODUuMTE1LjIKMTIwLjg1LjExNS40NgoxMjAuODUuMTE1LjQ5CjEyMC44NS4x\n" +
            "MTUuNjAKMTIwLjg1LjExNS43NQoxMjAuODUuMTE1LjEwNAoxMjAuODUuMTE1\n" +
            "LjE0OAoxMjAuODUuMTE1LjE3NQoxMjAuODUuMTE1LjE5NwoxMjAuODUuMTE1\n" +
            "LjIyNQoxMjAuODUuMTE2LjE3CjEyMC44NS4xMTYuNjkKMTIwLjg1LjExNi43\n" +
            "MAoxMjAuODUuMTE2LjEzMwoxMjAuODUuMTE2LjIzMAoxMjAuODUuMTE2LjIz\n" +
            "OAoxMjAuODUuMTE3LjIwNwoxMjAuODUuMTE4Ljc4CjEyMC44NS4xMTguMTYx\n" +
            "CjEyMC44NS4xMTguMTY5CjEyMC44NS4xMTguMTk1CjEyMC44NS4xMTguMjE5\n" +
            "CjEyMC44NS4xMTguMjI3CjEyMC44NS4xMTguMjM1CjEyMC44NS4xMTguMjM4\n" +
            "CjEyMC44NS4xNDguMjYKMTIwLjg1LjE0OS4xMTIKMTIwLjg1LjE3Mi4yNDkK\n" +
            "MTIwLjg2LjIzNi4yMTQKMTIwLjg2LjIzNi4yMTcKMTIwLjg2LjIzNy45NAox\n" +
            "MjAuODYuMjM3LjE2NgoxMjAuODYuMjM4LjQ3CjEyMC44Ni4yMzguMTg4CjEy\n" +
            "MC44Ni4yMzkuOTcKMTIwLjg2LjIzOS4xNTQKMTIwLjg2LjI1NC4xMzMKMTIw\n" +
            "Ljg2LjI1NC4xODgKMTIwLjg2LjI1NS4xMDkKMTIwLjg2LjI1NS4yNDcKMTIw\n" +
            "LjE5My45MS4xODMKMTIwLjE5My45MS4xOTAKMTIwLjE5My45MS4yMTUKMTIw\n" +
            "LjIyNi4yOC41MwoxMjAuMjI2LjI4LjU1CjEyMC4yMjYuMjguNTYKMTIwLjIz\n" +
            "OC4xODkuNzIKMTIwLjI0MC40OC44MwoxMjAuMjQwLjQ4LjkxCjEyMS41LjE1\n" +
            "NS4xNTgKMTIxLjQ2LjIzMi4xMzAKMTIxLjYxLjk4LjIyCjEyMS4yMDYuMTU0\n" +
            "LjEzMgoxMjIuOTYuMTIuMjAzCjEyMi4xMTYuMjI5LjIwOAoxMjIuMTE2LjI0\n" +
            "MC4xMjkKMTIyLjExNy4yOC4yMDAKMTIyLjExNy4yMTIuNjYKMTIyLjE0Ny4y\n" +
            "Mi4xNDYKMTIyLjE0Ny42Mi43NgoxMjIuMTczLjIzLjU1CjEyMi4xODguMTUw\n" +
            "LjIxCjEyMi4yNTQuMjkuMjMKMTIzLjEwLjE1LjM0CjEyMy4xMS4xNTIuOTMK\n" +
            "MTIzLjEyLjIzLjUKMTIzLjExMC4yMTMuNDEKMTIzLjIwNS4xNTYuMjEyCjEy\n" +
            "NC4xMzEuNTUuMTUKMTI0LjE1My4xMzYuMTc1CjEyNS40NC4xMS42OQoxMjUu\n" +
            "NjMuMTA1LjU1CjEyNS4xMjcuMTMyLjExMgoxMjUuMTI3LjEzOS42OQoxMjUu\n" +
            "MTI4LjI4LjE4MQoxMjUuMTY4LjE0Ny4yMDIKMTI1LjIyNC4xMjYuNDEKMTI1\n" +
            "LjIyOC4zMy4yMTEKMTI1LjIyOC40My4xOTcKMTI1LjIyOC44OS4xNzgKMTI1\n" +
            "LjIyOC45MC4yMjkKMTM0LjIwOS4yMTguMjAzCjEzNy4xODQuNjIuMTgwCjE0\n" +
            "MC4yMDYuODYuMTI0CjE0Ni43MC4zNC4yCjE1Ni4yNTEuMTM2LjQKMTU3LjYx\n" +
            "LjIxMi4xCjE1Ny42MS4yMTIuMjkKMTU3LjYxLjIxMi4zNwoxNTcuNjEuMjEy\n" +
            "LjQxCjE1Ny42MS4yMTIuNDQKMTU3LjYxLjIxMi40NwoxNTcuNjEuMjEyLjU1\n" +
            "CjE1Ny42MS4yMTIuNTcKMTU3LjYxLjIxMi41OQoxNTcuNjEuMjEyLjY0CjE1\n" +
            "Ny42MS4yMTIuNzgKMTU3LjYxLjIxMi44MgoxNTcuNjEuMjEyLjg0CjE1Ny42\n" +
            "MS4yMTIuODUKMTU3LjYxLjIxMi44NwoxNTcuNjEuMjEyLjg4CjE1Ny42MS4y\n" +
            "MTIuOTUKMTU3LjYxLjIxMi4xMDEKMTU3LjYxLjIxMi4xMDQKMTU3LjYxLjIx\n" +
            "Mi4xMDkKMTU3LjYxLjIxMi4xMTEKMTU3LjYxLjIxMi4xMTcKMTU3LjYxLjIx\n" +
            "Mi4xMjIKMTU3LjYxLjIxMy4xNDAKMTU3LjYxLjIxMy4xNDYKMTU3LjYxLjIx\n" +
            "My4xNDkKMTU3LjYxLjIxMy4xNjUKMTU3LjYxLjIxMy4xNzQKMTU3LjYxLjIx\n" +
            "My4xNzkKMTU3LjYxLjIxMy4yMzgKMTU3LjYxLjIxMy4yNDAKMTU5LjIwMy4x\n" +
            "ODYuMTU5CjE2MC4xMjQuMTM4LjE5MAoxNjEuMjIuMzQuMTE2CjE2MS45Ny4x\n" +
            "NDMuNTQKMTYzLjEyNS4yMTEuMTAzCjE2My4xMjUuMjExLjExOQoxNjMuMTI1\n" +
            "LjIxMS4xNDQKMTYzLjE3Mi4xNDAuMjAKMTYzLjE3Mi4xNzYuMTY4CjE2My4x\n" +
            "NzkuMTY3LjE1NQoxNjMuMjA0LjIxMS44MAoxNjQuMTU1Ljg4LjM0CjE2NS4y\n" +
            "MjcuNzQuNjEKMTY1LjIyNy44NC4yMzAKMTY3LjcxLjI0OS4xODQKMTY3LjE3\n" +
            "Mi41OS4yMDcKMTcwLjI0Ny43Ni4xNzgKMTcwLjI0Ny43Ni4xNzkKMTc0Ljgz\n" +
            "LjczLjE2MwoxNzUuOS4xMzUuMzMKMTc1LjEwLjE5LjMyCjE3NS4xMS42NC4y\n" +
            "NAoxNzUuMTgzLjQuMjMKMTc1LjE4My4xNi4xMzUKMTc2LjEwMy44OC41Nwox\n" +
            "NzYuMTExLjE3My4xMjIKMTc2LjExMS4xNzMuMTM5CjE3Ni4yMjEuMjA2LjEx\n" +
            "NQoxNzcuMTQ5LjE2NC4yNAoxNzguMTguMjU0LjIyOQoxNzguNzIuNjkuNzgK\n" +
            "MTc4LjcyLjcwLjg4CjE3OC43Mi43MC4xMTIKMTc4LjcyLjcwLjEyNAoxNzgu\n" +
            "NzIuNzAuMjA3CjE3OC43Mi43MC4yMzkKMTc4LjcyLjc2LjIKMTc4LjcyLjc4\n" +
            "LjE1NgoxNzguNzIuNzguMjAyCjE3OC4xNDEuMTQuMjE2CjE4MC4xNTEuMjQu\n" +
            "NjAKMTgwLjE3Ni45OS40OAoxODAuMTg4LjIzMi4xMjIKMTgwLjE4OC4yMzIu\n" +
            "MTM3CjE4MC4xODguMjM3LjE3MAoxODEuMTc2LjE1NS4yNQoxODEuMTk5LjE2\n" +
            "Mi43CjE4MS4xOTkuMTcwLjIyMgoxODIuNTIuMTM2LjQ1CjE4Mi4xMTUuMTcz\n" +
            "Ljk1CjE4Mi4xMjQuOTIuNzAKMTgyLjE1NS4xMjAuMTQzCjE4Mi4xNjYuMTgw\n" +
            "LjE5NAoxODIuMjM1LjIwOC4xMjQKMTgzLjgyLjE0NC4xMjYKMTgzLjE2MS4x\n" +
            "LjE5CjE4My4yMzcuMTQ2LjE3NQoxODMuMjM3LjE0Ni4yMDYKMTg0LjU4LjIz\n" +
            "My4xNzkKMTg1LjY4LjIzMC4yMDcKMTg1LjEyOC40MS41MAoxODUuMTQyLjIz\n" +
            "OS4xMzUKMTg1LjE5MS4zMi4xNTgKMTg1LjE5MS4yNDYuNDUKMTg1LjIzMi42\n" +
            "NC4zMgoxODYuMzMuOTAuMjQ5CjE4Ni4yNTAuMTE1LjkzCjE4Ny40NS4xMTYu\n" +
            "MTYyCjE4OC4xNjkuMzYuMjcKMTg4LjE2OS4xNzQuMTY2CjE5MC4xODAuMTU0\n" +
            "LjIyNwoxOTIuMy4xOTQuMjAyCjE5My4xNjkuMjUyLjE1OAoxOTMuMTY5LjI1\n" +
            "Mi4xNjYKMTkzLjE2OS4yNTIuMjQ1CjE5NS4yMDguMTU0LjkKMTk4Ljk4LjYy\n" +
            "LjQzCjIwMS43MS4xODYuMTc4CjIwMS4xODQuMTYuMjQ0CjIwMS4xODQuNDku\n" +
            "MjM0CjIwMS4xODQuNTQuMTc4CjIwMS4xODQuNTQuMTc5CjIwMS4xODQuNTQu\n" +
            "MTgwCjIwMS4xODQuNjQuMjM4CjIwMS4xODQuODkuOTgKMjAyLjEyOS41OC4x\n" +
            "MzAKMjAyLjE2NC4xMzguMTU3CjIwMi4xNjQuMTM5LjE2OAoyMDIuMTY0LjEz\n" +
            "OS4yMTgKMjAyLjE2NC4xMzkuMjI5CjIwMy4xNzYuMTI5LjczCjIwMy4yNDgu\n" +
            "MTc1LjcxCjIwMy4yNDguMTc1LjcyCjIwNi4xODkuMTExLjE2CjIwNy4xODAu\n" +
            "MjE5LjIzOAoyMTAuMTMuMTEwLjYwCjIxMC44OS42My4yMQoyMTAuODkuNjMu\n" +
            "MjMxCjIxMC44OS42My4yNDUKMjEwLjEwOC43MC4xMTkKMjEwLjE4MC4yMzcu\n" +
            "MjEyCjIxMS40Ny44My4yMDAKMjExLjE0OS4xOTEuMjA5CjIxMi4xMjkuMjYu\n" +
            "NAoyMTIuMTkzLjMwLjE0NAoyMTMuNS40Ny40MwoyMTYuNC45NS42MQoyMTYu\n" +
            "NC45NS42MgoyMTguMjkuMTI2LjUzCjIxOC4zMS4xMjMuOTAKMjE4LjE2MS4x\n" +
            "MDYuMjYKMjE5LjY4LjIzOC40OQoyMTkuNjkuMTEwLjIwNgoyMTkuMTM2LjE3\n" +
            "Mi4xNjEKMjE5LjEzOC4xNDAuMTE0CjIxOS4xNTcuMTM5LjE2NQoyMjAuMTMz\n" +
            "LjY0LjIzMwoyMjAuMTM0LjY0LjE2OQoyMjAuMTM0LjIwNi4xMzQKMjIwLjEz\n" +
            "NC4yMzYuNzgKMjIwLjEzNS4xMzUuNDQKMjIwLjEzNS4yNDEuMTIKMjIwLjE0\n" +
            "My4zMy4xMjkKMjIxLjEuMjI1LjE5MQoyMjEuMS4yMjYuMTUKMjIxLjE2MC4x\n" +
            "NzcuMTE5CjIyMS4yMzEuMTY5LjE0MQoyMjIuNzcuMTgxLjI4CjIyMi4xMTgu\n" +
            "NC4yOQoyMjIuMTM4LjExOS4xOTQKMjIyLjI0MC4xMTcuODgKMjIyLjI0Ny41\n" +
            "Ljc4CjIyMi4yNTMuNDUuMTQxCjIyMy4xMzAuMzEuNTcKMjIzLjE0OS4xLjIx\n" +
            "MQoyMjMuMTQ5LjE0MC4zNwoyMjMuMTU1LjM0LjEyNgoyMjMuMTU5Ljg4LjgK\n";
}
