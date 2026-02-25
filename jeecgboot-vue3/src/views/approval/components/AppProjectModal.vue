<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="title" @ok="handleSubmit" width="800px" destroyOnClose>
    <BasicForm @register="registerForm" />
    
    <div class="p-2" v-if="recordList.length > 0">
      <a-divider orientation="left">审批流转记录</a-divider>
      <BasicTable @register="registerTable" :dataSource="recordList" />
    </div>

    <!-- 自定义底部按钮，用于审批操作 -->
    <template #footer v-if="showApproveButtons">
      <a-button @click="closeModal">取消</a-button>
      <a-button type="primary" danger @click="handleReject" :loading="confirmLoading">驳回</a-button>
      <a-button type="primary" @click="handlePass" :loading="confirmLoading">通过</a-button>
    </template>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicTable, useTable } from '/@/components/Table';
  import { formSchema, appRecordColumns } from '../approval.data';
  import { saveOrUpdate, queryAppRecordByMainId } from '../approval.api';
  import { useUserStore } from '/@/store/modules/user';
  import { Divider as ADivider } from 'ant-design-vue';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);
  const showApproveButtons = ref(false);
  const confirmLoading = ref(false);
  const recordList = ref([]);
  const userStore = useUserStore();

  const title = computed(() => (!unref(isUpdate) ? '发起新申请' : (unref(showApproveButtons) ? '审批申请' : '详情')));

  const [registerForm, { resetFields, setFieldsValue, validate, updateSchema }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
  });

  const [registerTable] = useTable({
    columns: appRecordColumns,
    pagination: false,
    showIndexColumn: true,
    size: 'small',
    canResize: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    showApproveButtons.value = !!data?.showApproveButtons;
    recordList.value = [];

    if (unref(isUpdate)) {
      setFieldsValue({ ...data.record });
      // 加载子表数据
      try {
        const res = await queryAppRecordByMainId({ id: data.record.id });
        recordList.value = res || [];
      } catch (e) {
        console.error('加载记录失败', e);
      }
    } else {
      // 新增时自动填充
      setFieldsValue({
        applicant: userStore.getUserInfo.username,
        status: '待审批',
        currentApprover: 'admin', // 默认给admin审批
        ...data.record // 关键：合并从“发起审批”页面传过来的 type
      });
      // 检查如果已经有了 type，则将其字段锁定
      if (data.record?.type) {
         updateSchema({
           field: 'type',
           dynamicDisabled: true
         });
      } else {
         // 如果是从“我的申请”列表点新增，可能没有传type，则不锁定
         updateSchema({
           field: 'type',
           dynamicDisabled: false
         });
      }
    }
  });

  async function handlePass() {
    await doApprove('已通过', '同意');
  }

  async function handleReject() {
    await doApprove('已驳回', '驳回');
  }

  async function doApprove(status: string, action: string) {
    try {
      const values = await validate();
      confirmLoading.value = true;
      // 设置状态
      values.status = status;
      // 添加审批记录
      const record = {
        operator: userStore.getUserInfo.username,
        action: action,
        comment: '通过审批', // 这里可以加一个弹窗输入意见，为了简单先写死
        updateTime: new Date().getTime(),
      };
      values.appRecordList = [record];
      
      await saveOrUpdate(values, true);
      closeModal();
      emit('success');
    } finally {
      confirmLoading.value = false;
    }
  }

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await saveOrUpdate(values, unref(isUpdate));
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style scoped>
  .p-2 {
    padding: 0.5rem;
  }
</style>
