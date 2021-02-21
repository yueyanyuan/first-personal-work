import json

# 提取数据
with open('global_epidemic_statistics.json', 'r', encoding="utf-8")as f:
    data = json.load(f)


# 数据格式进行处理
data = data["data"]["continent"]

Asia = data[0]["country"]
Europe = data[1]["country"]
NorthAmerica = data[2]["country"]
SouthAmerica = data[3]["country"]
Africa = data[4]["country"]
Oceania = data[5]["country"]
country = Asia + Europe + NorthAmerica + SouthAmerica + Africa + Oceania
print(country)

countryList = []
list = []
# for i in range(len(country)):
#     name = country[i]["provinceName"]
#     value = country[i]["confirmedCount"]
#     countryDict = {}
#     countryDict["name"] = name
#     countryDict["value"] = value
#     countryList.append(countryDict)
# print(countryList)

for i in range(len(country)):
    name = country[i]["provinceName"]
    value = country[i]["confirmedCount"]

# 将国家名称中文转英文
    with open('country.json', 'r', encoding='utf-8') as f:
        data2 = json.load(f)
    try:
        countryDict = {}
        countryDict["name"] = data2[name]
        countryDict["value"] = value
        countryList.append(countryDict)
    except:
        list.append(name)
print(countryList)
# 数据保存

with open("global_epidemic_statistics_deal.json", 'w', encoding="utf-8") as f:
    json.dump(countryList, f)
    print("保存成功！")