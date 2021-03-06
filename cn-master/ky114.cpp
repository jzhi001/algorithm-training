#include <iostream>
#include <string>
#include <algorithm>
#include <cmath>
#include <vector>

using namespace std;

int main(){

    string a, b;

    while(cin >> a >> b){

        int n = a.size(), m = b.size();

        a = " " + a;
        b = " " + b;

        vector<vector<int>> f(n + 1, vector<int>(m + 1, 0));

        for(int i = 1; i <= n; i++){
            for(int j = 1; j <= m; j++){
                f[i][j] = max(f[i-1][j], f[i][j-1]);
                if(a[i] == b[j])
                    f[i][j] = max(f[i][j], f[i-1][j-1] + 1);
            }
        }

        cout << f[n][m] << endl;
    }

    return 0;
}