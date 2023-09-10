#include "plusaes.hpp"
#include <bits/stdc++.h>

using namespace std;
using namespace plusaes;

char \u02aa[25] = {-83,107,42,-58,104,-31,86,108,103,65,103,-6,-52,65,-107,-59,49,4,91,37,-124,-47,-4,110,119};

string \u02ab(int var0, int var1_1, int var2_2)
{
    var2_2 = var2_2 * 4 + 4;
    int var7_3 = var0 * 3 + 83;
    int var6_4 = -1;
    char* var3_5 = \u02aa;
    int var8_6 = var1_1 * 3 + 13;
    char var4_7[var8_6];
    var1_1 = var6_4;
    int var5_8 = var7_3;
    var0 = var2_2;
    bool boolean = true;
    do
    {
        if(!boolean)
        {
            var0 = var7_3 + 1;
            var5_8 = var2_2 + var5_8 + 1;
        }
        boolean = false;
        var4_7[++var1_1] = var5_8;
        if (var1_1 == var8_6 - 1)
        {
            return var4_7;
        }
        var2_2 = var5_8;
        var5_8 = var3_5[var0];
        var7_3 = var0;
    }
    while (true);
}

int hex_value(unsigned char hex_digit)
{
    static const signed char hex_values[256] =
    {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1,
            -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        };
    int value = hex_values[hex_digit];
    if (value == -1) throw invalid_argument("invalid hex digit");
    return value;
}

string string_to_hex(const string& input)
{
    static const char hex_digits[] = "0123456789ABCDEF";

    string output;
    output.reserve(input.length() * 2);
    for (unsigned char c : input)
    {
        output.push_back(hex_digits[c >> 4]);
        output.push_back(hex_digits[c & 15]);
        output.push_back(' ');
    }
    return output;
}

string hex_to_string(const string& input)
{
    const auto len = input.length();

    string output;
    output.reserve(len / 2);
    for (auto it = input.begin(); it != input.end(); )
    {
        if(*it == ' ')
        {
            *it++;
        }
        else
        {
            int hi = hex_value(*it++);
            int lo = hex_value(*it++);
            output.push_back(hi << 4 | lo);
        }
    }
    return output;
}

string to_string(const vector<unsigned char>& input)
{
    return  string(input.begin(), input.end());
}


vector<unsigned char> to_vector(string const& str)
{
    // don't forget the trailing 0...
    return vector<unsigned char>(str.data(), str.data() + str.length() + 1);
}

int main()
{
    // AES-GCM 128-bit

    const string plain_text = "Hello World!";

    vector<unsigned char> key = key_from_string(&"0123456789012345");

    const unsigned char iv[12] =
    {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0A, 0x0B
    };

    unsigned char tag[16] =
    {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0A, 0x0B, 0x00, 0x00, 0x00, 0x01
    };

    cout << "PlainText: " << plain_text << endl << endl;
    cout << "key: " << to_string(key) << endl << endl;

    // Encrypt ------------------------------------------------------------------------------------------------------------------------------

    encrypt_gcm((unsigned char*)plain_text.data(), plain_text.size(), 0, 0, &key[0], key.size(), &iv, &tag);

    cout << "CipherText: " << plain_text << endl << endl;
    cout << "CipherText HEX: " << string_to_hex(plain_text) << endl << endl;

    // Decrypt -----------------------------------------------------------------------------------------------------------------------------

    string cipher_text = hex_to_string("E8 91 70 B4 54 3A B9 C6 CF 92 C8 7E");

    decrypt_gcm((unsigned char*)cipher_text.data(), cipher_text.size(), 0, 0, &key[0], key.size(), &iv, &tag);
    cout << "PlainText: " << cipher_text << endl << endl;
}
