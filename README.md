# ComCode

### Common code auto generator

## Usage
### resources/common-code/codes.json
```json
[
  {
    "grp_com_nm": "회원 유형",
    "grp_com_cd": "USRT",
    "grp_com_desc": "사용자 유형 > 관리자/사용자",
    "com_cd": "AD",
    "com_cd_nm": "관리자",
    "com_cd_desc": "사용자 유형 관리자"
  },
  {
    "grp_com_nm": "회원 유형",
    "grp_com_cd": "USRT",
    "grp_com_desc": "사용자 유형 > 어드민/사용자",
    "com_cd": "UR",
    "com_cd_nm": "사용자",
    "com_cd_desc": "사용자 유형 관리자"
  }
...
]
```

### CommonCodes.java
```java
@CommonCodeBuilder(value = "common-codes/codes.json")
public class CommonCodes { }
```

### Complie
`$ maven compile`
