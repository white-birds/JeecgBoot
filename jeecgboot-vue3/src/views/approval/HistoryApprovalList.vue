<template>
  <div class="p-4">
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '查看详情',
              icon: 'ant-design:info-circle-outlined',
              onClick: handleView.bind(null, record),
            },
          ]"
        />
      </template>
    </BasicTable>
    <AppProjectModal @register="registerModal" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { list } from './approval.api';
  import { columns, searchFormSchema } from './approval.data';
  import AppProjectModal from './components/AppProjectModal.vue';

  const [registerModal, { openModal }] = useModal();
  
  const [registerTable] = useTable({
    title: '审批历史记录',
    api: list,
    beforeFetch: (params) => {
      // 过滤已经完成或驳回的记录
      // 注意：这里的过滤逻辑需要后端支持，这里仅做演示，可以通过 status 列表过滤
      return { 
        ...params, 
        // 假设后端支持状态 in 查询
        status_in: '已通过,已驳回'
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

  function handleView(record: Recordable) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }
</script>
