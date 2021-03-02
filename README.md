# sql2Pojo

根据SQL SELECT语句中的字段生成Java对象
目前仅支持SELECT语句


重要参数说明:
    sourcePath:生成的Java文件的路径
    filePath:SQL语句文件路径(目前仅支持一条语句一个文件)
    fileName:要生成的Java文件名