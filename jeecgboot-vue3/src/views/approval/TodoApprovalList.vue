<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '去审批',
              icon: 'ant-design:check-square-outlined',
              onClick: handleApprove.bind(null, record),
            },
          ]"
        />
      </template>
    </BasicTable>
    <AppProjectModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { list } from './approval.api';
  import { columns, searchFormSchema } from './approval.data';
  import AppProjectModal from './components/AppProjectModal.vue';
  import { useUserStore } from '/@/store/modules/user';

  const userStore = useUserStore();
  const [registerModal, { openModal }] = useModal();
  
  const [registerTable, { reload }] = useTable({
    title: '待我审批',
    api: list,
    beforeFetch: (params) => {
      // 过滤当前审批人为自己且状态为待审批的记录
      return { 
        ...params, 
        currentApprover: userStore.getUserInfo.username,
        status: '待审批'
      };
    },
    columns,
    formConfig: {
      labelWidth: 100,
      schemas: searchFormSchema,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    actionColumn: {
      width: 120,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
      fixed: 'right',
    },
  });

  function handleApprove(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
      showApproveButtons: true, // 显示审批按钮
    });
  }
</script>
