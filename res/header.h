struct sched_group {
        struct sched_group      *next;
        atomic_t                ref;

        unsigned int            group_weight;
        void *sgc;
        int                     asym_prefer_cpu;

        unsigned long           cpumask[];
};