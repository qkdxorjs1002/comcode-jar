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

### Generated Code
<img width="254" alt="image" src="https://github.com/user-attachments/assets/6f0dfcc6-e1b7-4490-b637-52f1d2ed7a60" />

### Inline
<img width="506" alt="image" src="https://github.com/user-attachments/assets/a6ee29c4-2a8e-4e38-8a26-dcf216e5263a" />

<img width="565" alt="image" src="https://github.com/user-attachments/assets/d1553802-df93-4ee1-b2e5-5ce2dfa78042" />
