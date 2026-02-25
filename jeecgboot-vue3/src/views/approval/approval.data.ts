import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

// 主表列定义
export const columns: BasicColumn[] = [
  {
    title: '申请标题',
    align: 'center',
    dataIndex: 'title',
    width: 200,
  },
  {
    title: '申请类型',
    align: 'center',
    dataIndex: 'type',
    width: 120,
    customRender: ({ text }) => {
      const color = text === '请假' ? 'blue' : text === '报销' ? 'purple' : 'cyan';
      return render.renderTag(text, color);
    },
  },
  {
    title: '申请人',
    align: 'center',
    dataIndex: 'applicant',
    width: 120,
  },
  {
    title: '状态',
    align: 'center',
    dataIndex: 'status',
    width: 100,
    customRender: ({ text }) => {
      let color = 'orange';
      if (text === '已通过') color = 'green';
      if (text === '已驳回') color = 'red';
      return render.renderTag(text, color);
    },
  },
  {
    title: '当前审批人',
    align: 'center',
    dataIndex: 'currentApprover',
    width: 120,
  },
  {
    title: '创建日期',
    align: 'center',
    dataIndex: 'createTime',
    width: 180,
  },
];

// 查询表单配置
export const searchFormSchema: FormSchema[] = [
  {
    label: '申请标题',
    field: 'title',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '申请类型',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [
        { label: '请假', value: '请假' },
        { label: '报销', value: '报销' },
        { label: '加班', value: '加班' },
      ],
    },
    colProps: { span: 6 },
  },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      options: [
        { label: '待审批', value: '待审批' },
        { label: '已通过', value: '已通过' },
        { label: '已驳回', value: '已驳回' },
      ],
    },
    colProps: { span: 6 },
  },
];

// 表单弹窗配置
export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'Input',
    show: false,
  },
  {
    label: '申请标题',
    field: 'title',
    component: 'Input',
    required: true,
  },
  {
    label: '申请类型',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [
        { label: '请假', value: '请假' },
        { label: '报销', value: '报销' },
        { label: '加班', value: '加班' },
      ],
    },
    required: true,
  },
  {
    label: '申请内容',
    field: 'content',
    component: 'InputTextArea',
    componentProps: {
      rows: 4,
      placeholder: '请输入申请详细内容...',
    },
    required: true,
  },
  {
    label: '审批人账号',
    field: 'currentApprover',
    component: 'Input',
    required: true,
    helpMessage: '请输入负责审批此单据的用户登录账号（如：admin）',
  },
  {
    label: '申请人账号',
    field: 'applicant',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      options: [
        { label: '待审批', value: '待审批' },
        { label: '已通过', value: '已通过' },
        { label: '已驳回', value: '已驳回' },
      ],
    },
    defaultValue: '待审批',
    dynamicDisabled: true, // 强制状态为只读
  },
];

// 子表（审批记录）列定义
export const appRecordColumns: BasicColumn[] = [
  {
    title: '操作人',
    align: 'center',
    dataIndex: 'operator',
    width: 120,
  },
  {
    title: '操作动作',
    align: 'center',
    dataIndex: 'action',
    width: 100,
    customRender: ({ text }) => {
      const color = text === '提交' ? 'blue' : text === '同意' ? 'green' : 'red';
      return render.renderTag(text, color);
    },
  },
  {
    title: '审批意见',
    align: 'left',
    dataIndex: 'comment',
  },
  {
    title: '操作时间',
    align: 'center',
    dataIndex: 'updateTime',
    width: 180,
  },
];
