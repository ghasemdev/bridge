#include "plusaes.hpp"
#include <bits/stdc++.h>

using namespace std;
using namespace plusaes;

const string plain_text = "Hello World";

const unsigned char iv[12] =
{
    5, 120, 63, 35,
    0, 65, 23, 34,
    73, 85, 160, 0
};

unsigned char tag[16] =
{
    126, 0, 35, 67, 95,21,20, 69,
    84, 52, 10, 6, 36, 84, 20, 99
};

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

vector<unsigned char> foo(int var_1, int var_2, int var_3)
{
    char m_1 = 30 + var_3;
    char m_2 = m_1;
    vector<unsigned char> a;
    a.push_back(36);
    a.push_back(126);
    a.push_back(53);
    char var_2_1 = var_1 * 3 + 5;
    bool boolean = true;
    do
    {
        if(boolean)
        {
            a.push_back((char)m_1);
            m_1 += var_2_1 - 3;
        }
        else
        {
            char var_1_1 = 2;
            m_2 -= var_1_1;
            a.push_back((char)m_2);

        }
        boolean != boolean;
        if(var_2 == a.size())
        {
            return a;
        }
        var_2_1++;
    }
    while (true);
    return a;
}

int main()
{
    vector<unsigned char> key = foo(0, 32, 7);
    cout << "PlainText: " << plain_text << endl << endl;
    cout << "key: " << to_string(key) << endl << endl;

    // Encrypt ------------------------------------------------------------------------------------------------------------------------------

    encrypt_gcm((unsigned char*)plain_text.data(), plain_text.size(), 0, 0, &key[0], key.size(), &iv, &tag);

    cout << "CipherText: " << plain_text << endl << endl;
    cout << "CipherText HEX: " << string_to_hex(plain_text) << endl << endl;

    // Decrypt -----------------------------------------------------------------------------------------------------------------------------

    string cipher_text = hex_to_string("13 4E 00 AE 4E DA C5 AB 34 3D 2F");
    decrypt_gcm((unsigned char*)cipher_text.data(), cipher_text.size(), 0, 0, &key[0], key.size(), &iv, &tag);
    cout << "PlainText: " << cipher_text << endl << endl;
}
