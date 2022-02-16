package com.myszon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpAddressProcessorHelperTest {


    @Test
    public void getIpAddressAndOrMask_shouldReturnIPAddressOnly_whenNoMaskInString() {
        // Arrange
        String expected = "127.0.0.1";

        // Act
        String[] actual = IpAddressProcessorHelper.getIpAddressAndOrMask(expected);

        // Assert
        assertNull(actual[1]);
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getIpAddressAndOrMask_shouldReturnIPAddressAndMask_whenNoMaskInString() {
        // Arrange
        String mask = "27";
        String ipAddress = "127.0.0.1";

        // Act
        String[] actual = IpAddressProcessorHelper.getIpAddressAndOrMask(ipAddress + "/" + mask);

        // Assert
        assertEquals(mask, actual[1]);
        assertEquals(ipAddress, actual[0]);
    }


    @Test
    public void getIpAddressAndOrMask_shouldReturnArrayOfNull_whenNotValidString() {
        // Arrange
        String mask = "272";
        String ipAddress = "12720.1";

        // Act
        String[] actual = IpAddressProcessorHelper.getIpAddressAndOrMask(ipAddress + "/" + mask);

        // Assert
        assertNull(actual[1]);
        assertNull(actual[0]);
    }

    @Test
    public void isValidIPAddress_shouldReturnFalse_whenNullIpAddress() {
        // Arrange
        String ipAddress = null;


        // Act
        boolean actual = IpAddressProcessorHelper.isValidIPAddress(ipAddress);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void isValidIPAddress_shouldReturnFalse_whenIPAddressIsTooShort() {
        // Arrange
        String ipAddress = "127.1.1";

        // Act
        boolean actual = IpAddressProcessorHelper.isValidIPAddress(ipAddress);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void isValidIPAddress_shouldReturnFalse_whenSegmentNotNumber() {
        // Arrange
        String ipAddress = "127.a.1.1";

        // Act
        boolean actual = IpAddressProcessorHelper.isValidIPAddress(ipAddress);

        // Assert
        assertFalse(actual);
    }


    @Test
    public void isValidIPAddress_shouldReturnFalse_whenSegmentOutOfTooBig() {
        // Arrange
        String ipAddress = "127.256.1.1";

        // Act
        boolean actual = IpAddressProcessorHelper.isValidIPAddress(ipAddress);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void isValidIPAddress_shouldReturnFalse_whenSegmentOutOfTooSmall() {
        // Arrange
        String ipAddress = "127.-1.1.1";

        // Act
        boolean actual = IpAddressProcessorHelper.isValidIPAddress(ipAddress);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void ipToLong_shouldGetCorrectNumberRepresentation() {
        // Arrange
        String ipAddress = "255.255.255.255";

        // Act
        long actual = IpAddressProcessorHelper.ipToLong(ipAddress);

        // Assert
        assertEquals(4294967295L, actual);

    }

    @Test
    public void ipToLong_shouldGetCorrectNumberRepresentation2() {
        // Arrange
        String ipAddress = "1.1.1.1";

        // Act
        long actual = IpAddressProcessorHelper.ipToLong(ipAddress);

        // Assert
        assertEquals(16843009L, actual);

    }


    @Test
    public void longToIP_shouldTransformFromLongToString() {
        // Arrange
        long ipAddress = 16843009L;

        // Act
        String actual = IpAddressProcessorHelper.longToIP(ipAddress);

        // Assert
        assertEquals("1.1.1.1", actual);
    }

    @Test
    public void longToIP_shouldTransformFromLongToString2() {
        // Arrange
        long ipAddress = 4294967295L;

        // Act
        String actual = IpAddressProcessorHelper.longToIP(ipAddress);

        // Assert
        assertEquals("255.255.255.255", actual);
    }
}