#include <iostream>
#include <string>

using namespace std;

char \u02aa[] = {-83,107,42,-58,104,-31,86,108,103,65,103,-6,-52,65,-107,-59,49,4,91,37,-124,-47,-4,110,119,17,97,-88,-86,104,-113,13,38,32,47,49,124,-79,-36,29,1,-59,-56,105,93,-17,-89,-122,-52,88,119,-110,-76,91,3,-10,-23,39,-98,34,-3,-82,114,-34,-43,-124,-127,104,58,85,108,82,-6,119,3,85,-89,-22,-126,-90,-33,38,105,-125,-1,-23,56,-120,121,-39,58,-85,106,-104,72,79,18,-82,34,41};

string \u02ab(int var0, int var1_1, int var2_2)
{
    var2_2 = var2_2 * 4 + 4;
    int var7_3 = var0 * 3 + 85;
    int var6_4 = -1;
    char* var3_5 = \u02aa;
    int var8_6 = var1_1 * 3 + 24;
    char var4_7[var8_6];
    var1_1 = var6_4;
    int var5_8 = var7_3;
    var0 = var2_2;
    bool firsttime = true;
    do {
        if(!firsttime){
            var0 = var7_3 + 5;
            var5_8 = var2_2 + var5_8 + 2;
        }
        firsttime=false;
        var4_7[++var1_1] = var5_8;
        if (var1_1 == var8_6 - 1) {
            return var4_7;
        }
        var2_2 = var5_8;
        var5_8 = var3_5[var0];
        var7_3 = var0;
    } while (true);
}

int main()
{
    cout << \u02ab(777, 10, 13) << endl;
    return 0;
}
