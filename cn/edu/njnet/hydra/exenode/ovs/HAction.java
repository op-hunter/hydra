package cn.edu.njnet.hydra.exenode.ovs;

public enum HAction {
	DROP(0),
	OUTPUT(1),
	GOTO_TABLE(2),
	WRITE_METADATA(3),
	METER(4);
    private int value = 0;

    private HAction(int value) {    //    ������private�ģ�����������
        this.value = value;
    }

    public static HAction valueOf(int value) {    //    ��д�Ĵ�int��enum��ת������
        switch (value) 
        {
        case 0:
            return DROP;
        case 1:
            return OUTPUT;
        case 2:
            return DROP;
        case 3:
            return OUTPUT;
        case 4:
            return DROP;
        default:
            return null;
        }
    }

    public int value() {
        return this.value;
    }
}
