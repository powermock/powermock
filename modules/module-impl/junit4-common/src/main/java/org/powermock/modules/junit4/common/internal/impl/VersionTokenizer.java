package org.powermock.modules.junit4.common.internal.impl;

// VersionTokenizer.java
class VersionTokenizer {
    private final String _versionString;
    private final int _length;

    private int _position;
    private int _number;
    private String _suffix;

    public int getNumber() {
        return _number;
    }

    public String getSuffix() {
        return _suffix;
    }

    public VersionTokenizer(String versionString) {
        if (versionString == null)
            throw new IllegalArgumentException("versionString is null");

        _versionString = versionString;
        _length = versionString.length();
    }

    public boolean MoveNext() {
        _number = 0;
        _suffix = "";

        // No more characters
        if (_position >= _length)
            return false;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c < '0' || c > '9') break;
            _number = _number * 10 + (c - '0');
            _position++;
        }

        int suffixStart = _position;

        while (_position < _length) {
            char c = _versionString.charAt(_position);
            if (c == '.') break;
            _position++;
        }

        _suffix = _versionString.substring(suffixStart, _position);

        if (_position < _length) _position++;

        return true;
    }
}
