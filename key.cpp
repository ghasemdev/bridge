#include <iostream>
#include <string>

using namespace std;

char \u02aa[] = {-83,107,42,-58,104,-31,86,108,103,65,103,-6,-52,65,-107,-59,49,4,91,37,-124,-47,-4,110,119};

string \u02ab(int var0, int var1_1, int var2_2)
{
    var2_2 = var2_2 * 4 + 4;
    int var7_3 = var0 * 3 + 83;
    int var6_4 = -1;
    char* var3_5 = \u02aa;
    int var8_6 = var1_1 * 3 + 21;
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

string encryptDecrypt(string toEncrypt)
{
    string key = \u02ab(0, 0, 0);
    string output = toEncrypt;

    for (int i = 0; i < toEncrypt.size(); i++)
        output[i] = toEncrypt[i] ^ key[i % (sizeof(key) / sizeof(char))];

    return output;
}

int main()
{
    string key = \u02ab(0, 0, 0);
    cout << "Key: " << key << endl << endl;

    string encrypted = encryptDecrypt("ghasem");
    cout << "Encrypted:" << encrypted << "\n";

    string decrypted = encryptDecrypt(encrypted);
    cout << "Decrypted:" << decrypted << "\n";

    return 0;
}
